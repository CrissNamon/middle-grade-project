package ru.danilarassokhin.game.exception;

import java.sql.SQLException;

import lombok.Getter;

@Getter
public class DataSourceConnectionException extends RuntimeException {

  private final SQLException cause;

  public DataSourceConnectionException(SQLException cause) {
    super(cause);
    this.cause = cause;
  }
}
