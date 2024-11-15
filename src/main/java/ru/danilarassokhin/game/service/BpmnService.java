package ru.danilarassokhin.game.service;

import java.util.List;

import ru.danilarassokhin.game.model.dto.ActionDto;
import ru.danilarassokhin.game.model.dto.SequenceActionDto;

/**
 * Service for Camunda BPMN parsing.
 */
public interface BpmnService {

  /**
   * Predicts next actions based on active nodes.
   * @param version Version of BPMN process
   * @param activeActions Active nodes of process
   * @return {@link ActionDto}
   */
  List<ActionDto> predictActions(Integer version, List<SequenceActionDto> activeActions);
}
