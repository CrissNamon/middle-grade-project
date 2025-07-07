package ru.danilarassokhin.game.exception;

/**
 * Exception for Camunda errors.
 */
public class CamundaException extends RuntimeException {

  public CamundaException(Throwable cause) {
    super(cause);
  }

  public CamundaException(String message, Throwable cause) {
    super(message, cause);
  }
}
