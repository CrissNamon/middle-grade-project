package ru.danilarassokhin.injection.exception;

/**
 * Exceptions for application.
 */
public class ApplicationException extends RuntimeException {

  public ApplicationException(String message, Throwable cause) {
    super(message, cause);
  }

  public ApplicationException(String message) {
    super(message);
  }

  public ApplicationException(Throwable cause) {
    super(cause);
  }
}
