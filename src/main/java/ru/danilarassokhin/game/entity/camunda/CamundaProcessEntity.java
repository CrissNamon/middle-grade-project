package ru.danilarassokhin.game.entity.camunda;

/**
 * Represents process in Camunda.
 * @param processInstanceKey Process instance key from Camunda
 */
public record CamundaProcessEntity(Long processInstanceKey) {}
