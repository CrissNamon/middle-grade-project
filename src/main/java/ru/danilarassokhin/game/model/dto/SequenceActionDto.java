package ru.danilarassokhin.game.model.dto;

/**
 * Represents node in Camunda.
 * @param id ID of node in Camunda
 * @param type {@link BpmnFlowNodeType}
 */
public record SequenceActionDto(String id, BpmnFlowNodeType type) {

}
