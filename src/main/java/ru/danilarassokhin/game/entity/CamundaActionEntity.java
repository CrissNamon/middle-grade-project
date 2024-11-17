package ru.danilarassokhin.game.entity;

/**
 * Represents action in Camunda.
 * @param id ID of action
 * @param taskId Unique task id in Camunda
 * @param formKey ID of UI activity
 */
public record CamundaActionEntity(String id, String taskId, String formKey) {}
