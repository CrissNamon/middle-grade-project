package ru.danilarassokhin.sql.exception;

/**
 * Exception for database integrity errors.
 */
public class DataIntegrityException extends RuntimeException {

  public DataIntegrityException(Throwable cause) {
    super(cause);
  }

}
