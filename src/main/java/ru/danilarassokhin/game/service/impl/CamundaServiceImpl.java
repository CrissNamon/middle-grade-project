package ru.danilarassokhin.game.service.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.EventSubscription;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.IntermediateCatchEvent;
import org.camunda.bpm.model.bpmn.instance.Lane;
import org.camunda.bpm.model.bpmn.instance.LaneSet;
import ru.danilarassokhin.game.config.ApplicationConfig;
import ru.danilarassokhin.game.config.CamundaConfig;
import ru.danilarassokhin.game.model.ActionDto;
import ru.danilarassokhin.game.service.CamundaService;
import ru.danilarassokhin.game.util.PropertiesFactory;
import tech.hiddenproject.progressive.annotation.Autofill;
import tech.hiddenproject.progressive.annotation.GameBean;

@GameBean
@Slf4j
public class CamundaServiceImpl implements CamundaService {

  private final RuntimeService runtimeService;
  private final RepositoryService repositoryService;
  private final PropertiesFactory propertiesFactory;

  private final String processId;

  @Autofill
  public CamundaServiceImpl(RuntimeService runtimeService, RepositoryService repositoryService,
                            PropertiesFactory propertiesFactory) {
    this.runtimeService = runtimeService;
    this.repositoryService = repositoryService;
    this.propertiesFactory = propertiesFactory;
    this.processId = propertiesFactory.getAsString(ApplicationConfig.CAMUNDA_PROCESS_ID_PROPERTY)
        .orElseThrow();
    createDeployments();
  }

  @Override
  public void startProcess(UUID id) {
    runtimeService.createProcessInstanceByKey(processId)
        .businessKey(id.toString())
        .execute();
  }

  @Override
  public List<ActionDto> getActions(UUID businessKey) {
    var processInstance = runtimeService.createProcessInstanceQuery()
        .processInstanceBusinessKey(businessKey.toString())
        .singleResult();
    var events = getAllEvents(processInstance);
    var flowNodes = getFlowNodes(processInstance);
    return events.stream()
        .map(eventSubscription -> {
          var flowNode = flowNodes.get(eventSubscription.getActivityId());
          if (Objects.nonNull(flowNode)) {
            var lane = flowNode.getLeft();
            var flow = flowNode.getRight();
            if (flow instanceof IntermediateCatchEvent) {
              return ImmutablePair.of(lane.getName(), eventSubscription.getEventName());
            }
          }
          return null;
        })
        .filter(Objects::nonNull)
        .map(this::flowNodeToActionDto)
        .toList();
  }

  private ActionDto flowNodeToActionDto(ImmutablePair<String, String> flowNode) {
    return new ActionDto(flowNode.getRight());
  }

  private void createDeployments() {
    String[] deployments = propertiesFactory.getAsStringArray(
        CamundaConfig.CAMUNDA_DEPLOYMENTS_PROPERTY_NAME,
        ApplicationConfig.DEFAULT_PROPERTY_DELIMITER
    ).orElse(new String[0]);
    var deploymentBuilder = repositoryService.createDeployment();
    Arrays.stream(deployments).forEach(deploymentBuilder::addClasspathResource);
    log.info("Deployed camunda processes: {}", deploymentBuilder.deployWithResult()
        .getDeployedProcessDefinitions().stream()
          .map(ProcessDefinition::getName).collect(Collectors.toSet()));
  }

  private List<EventSubscription> getAllEvents(ProcessInstance processInstance) {
    return runtimeService.createEventSubscriptionQuery()
        .processInstanceId(processInstance.getProcessInstanceId())
        .list();
  }

  private Map<String, ImmutablePair<Lane, FlowNode>> getFlowNodes(ProcessInstance processInstance) {
    var bpmnModel = repositoryService.getBpmnModelInstance(processInstance.getProcessDefinitionId());
    var laneSets = bpmnModel.getModelElementsByType(LaneSet.class);
    return laneSets.stream()
        .flatMap(laneSet -> laneSet.getLanes().stream()
            .flatMap(lane -> getDeepFlowNodes(lane.getFlowNodeRefs()).stream()
                .map(flowNode -> ImmutablePair.of(lane, flowNode))))
        .collect(Collectors.toMap(pair -> pair.getRight().getId(), Function.identity()));
  }

  private Collection<FlowNode> getDeepFlowNodes(Collection<FlowNode> flowNodes) {
    var deepFlowNodes = flowNodes.stream().flatMap(flowNode -> {
      var deepNodes = getDeepFlowNodes(flowNode.getChildElementsByType(FlowNode.class));
      return deepNodes.stream();
    }).toList();
    flowNodes.addAll(deepFlowNodes);
    return flowNodes;
  }

}
