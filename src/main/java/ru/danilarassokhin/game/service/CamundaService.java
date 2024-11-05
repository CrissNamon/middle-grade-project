package ru.danilarassokhin.game.service;

import java.util.List;
import java.util.UUID;

import ru.danilarassokhin.game.model.ActionDto;

/**
 * Service to work with Camunda engine.
 */
public interface CamundaService {

  /**
   * Start process with business key.
   * @param id Business key
   */
  void startProcess(UUID id);

  /**
   * Gets available actions in process.
   * @param businessKey Process business key
   * @return List of {@link ActionDto}
   */
  List<ActionDto> getActions(UUID businessKey);

  /**
   * Sends event to Camunda process.
   * @param actionDto {@link ActionDto}
   */
  void doAction(ActionDto actionDto);

}
