package ru.danilarassokhin.game.service;

import java.util.List;

import ru.danilarassokhin.game.model.dto.CamundaActionDto;

/**
 * Service for operations with Camunda.
 */
public interface CamundaService {

  /**
   * Creates new process instance with given business key.
   * @param businessKey Unique identifier of the process
   */
  void createProcess(Integer businessKey);

  /**
   * Gets available actions in process instance.
   * @param businessKey Unique identifier of the process
   * @return List of {@link CamundaActionDto}
   */
  List<CamundaActionDto> getAvailableActions(Integer businessKey);

  /**
   * Executes action in process instance.
   * @param camundaActionDto {@link CamundaActionDto}
   */
  void doAction(CamundaActionDto camundaActionDto);

}
