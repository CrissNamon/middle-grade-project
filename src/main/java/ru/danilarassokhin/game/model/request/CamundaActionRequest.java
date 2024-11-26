package ru.danilarassokhin.game.model.request;

import jakarta.validation.constraints.NotEmpty;

/**
 * Represents action in Camunda.
 * @param id ID of action
 * @param taskId Unique task id in Camunda
 * @param formKey ID of UI activity
 */
public record CamundaActionRequest(
    @NotEmpty String id,
    @NotEmpty String taskId,
    @NotEmpty String formKey
) {}
