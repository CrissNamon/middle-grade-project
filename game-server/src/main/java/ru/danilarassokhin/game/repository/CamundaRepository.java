package ru.danilarassokhin.game.repository;

import java.util.List;
import java.util.Map;

import ru.danilarassokhin.game.entity.camunda.CamundaActionEntity;
import ru.danilarassokhin.game.entity.camunda.CamundaProcessEntity;

/**
 * Repository for Camunda operations.
 */
public interface CamundaRepository {

  /**
   * Creates new process instance with given business key.
   * @param processId ID of bpmn process
   * @param variables Start variables for this process
   * @return {@link CamundaProcessEntity}
   */
  CamundaProcessEntity createProcess(String processId, Map<String, Object> variables);

  /**
   * Gets available actions in process instance.
   * @param businessKey Unique identifier of the process
   * @return List of {@link CamundaActionEntity}
   */
  List<CamundaActionEntity> getAvailableActions(Integer businessKey);

  /**
   * Executes action in process instance.
   * @param action {@link CamundaActionEntity}
   */
  void doAction(CamundaActionEntity action);

  /**
   * Deploys new process.
   * @param classpathResource Classpath resource with bpmn schema.
   */
  void deployProcess(String classpathResource);

  /**
   * Sends signal to Camunda.
   * @param signalId ID of signal
   */
  void broadcastSignal(String signalId);

}
