package ru.danilarassokhin.game.exception;

/**
 * Exception for database errors.
 */
public class DataSourceException extends RuntimeException {

  public DataSourceException(Throwable cause) {
    super(cause);
  }

  public DataSourceException(String message) {
    super(message);
  }
}
