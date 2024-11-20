package ru.danilarassokhin.game.model.dto;

/**
 * Represents action in Camunda.
 * @param id ID of action
 * @param taskId Unique task id in Camunda
 * @param formKey ID of UI activity
 */
public record CamundaActionDto(String id, String taskId, String formKey) {}
