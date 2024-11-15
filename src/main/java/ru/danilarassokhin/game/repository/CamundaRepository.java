package ru.danilarassokhin.game.repository;

import java.util.List;
import java.util.concurrent.Future;

import ru.danilarassokhin.game.entity.CamundaActionEntity;
import ru.danilarassokhin.game.entity.CamundaProcessEntity;

/**
 * Repository for Camunda operations.
 */
public interface CamundaRepository {

  /**
   * Creates new process instance with given business key.
   * @param businessKey Unique identifier of the process
   * @return {@link CamundaProcessEntity} future
   */
  Future<CamundaProcessEntity> createProcess(Integer businessKey);

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

}
