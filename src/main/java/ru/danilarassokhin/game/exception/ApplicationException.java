package ru.danilarassokhin.game.exception;

/**
 * Exceptions for application.
 */
public class ApplicationException extends RuntimeException {

  public ApplicationException(String message, Throwable cause) {
    super(message, cause);
  }
}
