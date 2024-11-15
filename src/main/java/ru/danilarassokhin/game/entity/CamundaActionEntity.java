package ru.danilarassokhin.game.entity;

/**
 * Represents action in Camunda.
 * @param id ID of action
 * @param taskId Unique task id in Camunda
 */
public record CamundaActionEntity(String id, String taskId) {}
