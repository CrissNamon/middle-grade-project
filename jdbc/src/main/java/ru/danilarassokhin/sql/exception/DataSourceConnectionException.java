package ru.danilarassokhin.sql.exception;

/**
 * Represents connection issues with Database.
 */
public class DataSourceConnectionException extends RuntimeException {

  public DataSourceConnectionException(Throwable cause) {
    super(cause);
  }
}
