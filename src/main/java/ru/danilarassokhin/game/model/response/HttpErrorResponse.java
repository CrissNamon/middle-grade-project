package ru.danilarassokhin.game.model.response;

/**
 * Response dto for errors.
 * @param message Error message
 */
public record HttpErrorResponse(String message) {}
