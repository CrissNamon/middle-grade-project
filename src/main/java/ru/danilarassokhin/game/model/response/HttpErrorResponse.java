package ru.danilarassokhin.game.model.response;

/**
 * DTO for error response from controller.
 * @param message Error message
 */
public record HttpErrorResponse(String message) {}
