package ru.danilarassokhin.game.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import io.camunda.zeebe.model.bpmn.Bpmn;
import io.camunda.zeebe.model.bpmn.instance.EventBasedGateway;
import io.camunda.zeebe.model.bpmn.instance.IntermediateCatchEvent;
import io.camunda.zeebe.model.bpmn.instance.Message;
import io.camunda.zeebe.model.bpmn.instance.Participant;
import io.camunda.zeebe.model.bpmn.instance.SequenceFlow;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.model.xml.ModelInstance;
import ru.danilarassokhin.game.exception.CamundaException;
import ru.danilarassokhin.game.model.camunda.BpmnEvent;
import ru.danilarassokhin.game.model.dto.ActionDto;
import ru.danilarassokhin.game.model.dto.SequenceActionDto;
import ru.danilarassokhin.game.service.BpmnService;

/**
 * Implementation of {@link BpmnService} for Camunda Platform 8. Based on BPMN parsing.
 */
@Slf4j
public class BpmnServiceImpl implements BpmnService {

  private final static String MESSAGE_REF_ATTRIBUTE_NAME = "messageRef";
  private final static String PROCESS_NAME_VERSION_DELIMITER = "__";

  private final Map<Integer, Map<String, BpmnEvent>> modelEvents = new HashMap<>();
  private final Map<Integer, Map<String, String>> modelMessages = new HashMap<>();
  private final Map<Integer, Map<String, Set<String>>> modelFlows = new HashMap<>();

  public BpmnServiceImpl(List<String> deployments) {
    deployments.forEach(this::initModel);
  }

  @Override
  public List<ActionDto> predictActions(Integer version, List<SequenceActionDto> activeActions) {
    return activeActions.stream()
        .flatMap(sequenceActionDto -> predictActionsFromSequenceNode(version, sequenceActionDto).stream())
        .toList();
  }

  private void initModel(String resource) {
    try (var inputStream = getClass().getClassLoader().getResourceAsStream(resource)) {
      if (Objects.isNull(inputStream)) {
        throw new CamundaException("Camunda deployment not found: " + resource);
      }
      var modelInstance = Bpmn.readModelFromStream(inputStream)
          .getDefinitions()
          .getModelInstance();
      var version = getKeyFromModel(modelInstance);
      modelMessages.put(version, getMessagesFromModel(modelInstance));
      modelEvents.put(version, getEventsFromModel(modelInstance));
      modelFlows.put(version, getFlowsFromModel(modelInstance));
    } catch (IOException e) {
      throw new CamundaException(e);
    }
  }

  private List<ActionDto> predictActionsFromSequenceNode(Integer version, SequenceActionDto sequenceActionDto) {
    return predictActionsFromNode(version, sequenceActionDto.id());
  }

  private List<ActionDto> predictActionsFromNode(Integer version, String nodeId) {
    return modelFlows.getOrDefault(version, new HashMap<>()).getOrDefault(nodeId, new HashSet<>()).stream()
        .filter(Objects::nonNull)
        .map(targetId -> modelEvents.getOrDefault(version, new HashMap<>()).get(targetId))
        .filter(Objects::nonNull)
        .map(bpmnEvent -> createActionFromBpmnEvent(version, bpmnEvent))
        .filter(Objects::nonNull)
        .toList();
  }

  private ActionDto createActionFromBpmnEvent(Integer version, BpmnEvent bpmnEvent) {
    var eventId = modelMessages.getOrDefault(version, new HashMap<>()).get(bpmnEvent.messageRef());
    if (eventId != null) {
      return new ActionDto(eventId, bpmnEvent.name());
    }
    return null;
  }

  private Map<String, Set<String>> getFlowsFromModel(ModelInstance modelInstance) {
    return modelInstance.getModelElementsByType(SequenceFlow.class).stream()
        .filter(this::isSequenceFlowForEvents)
        .collect(HashMap::new, (map, flow) ->
            map.compute(flow.getSource().getId(), (source, targets) -> {
              if (Objects.isNull(targets)) {
                return new HashSet<>(){{ add(flow.getTarget().getId()); }};
              }
              targets.add(flow.getTarget().getId());
              return targets;
            }), HashMap::putAll);
  }

  private boolean isSequenceFlowForEvents(SequenceFlow sequenceFlow) {
    return (sequenceFlow.getSource() instanceof IntermediateCatchEvent ||
        sequenceFlow.getSource() instanceof EventBasedGateway) &&
            sequenceFlow.getTarget() instanceof IntermediateCatchEvent;
  }

  private Map<String, String> getMessagesFromModel(ModelInstance modelInstance) {
    return modelInstance.getModelElementsByType(Message.class).stream()
        .collect(HashMap::new, (map, message) -> map.put(message.getId(), message.getName()), HashMap::putAll);
  }

  private Map<String, BpmnEvent> getEventsFromModel(ModelInstance modelInstance) {
    return modelInstance.getModelElementsByType(IntermediateCatchEvent.class).stream()
        .collect(HashMap::new,
                 (map, event) -> getMessageRefFromEvent(event)
                     .ifPresent(messageRef ->
                                    map.put(event.getId(), new BpmnEvent(event.getName(), messageRef))),
                 HashMap::putAll);
  }

  private Optional<String> getMessageRefFromEvent(IntermediateCatchEvent event) {
    return event.getEventDefinitions()
        .stream().map(eventDefinition -> eventDefinition.getAttributeValue(MESSAGE_REF_ATTRIBUTE_NAME))
        .findFirst();
  }

  private Integer getKeyFromModel(ModelInstance modelInstance) {
    var processName = modelInstance.getModelElementsByType(Participant.class).stream()
        .findFirst()
        .map(participant -> participant.getProcess().getName())
        .orElseThrow();
    var nameAndVersion = processName.split(PROCESS_NAME_VERSION_DELIMITER);
    if (nameAndVersion.length != 2) {
      throw new CamundaException("Wrong process name! Must be <String:name>__<Int:version>");
    }
    return Integer.valueOf(nameAndVersion[1]);
  }
}
