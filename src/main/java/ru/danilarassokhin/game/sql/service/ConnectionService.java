package ru.danilarassokhin.game.sql.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface ConnectionService {

  /**
   * Executes update query with prepared statement.
   * @param connection {@link Connection} to use for query
   * @param query SQL query
   * @param args SQL query arguments
   * @return either (1) the row count for SQL Data Manipulation Language (DML) statements or (2) 0
   * for SQL statements that return nothing
   */
  int executeUpdate(Connection connection, String query, Object... args) throws SQLException;

  /**
   * Executes query with prepared statement.
   * @param connection {@link Connection} to use for query
   * @param query SQL query
   * @param processor Mapper for query result
   * @param args SQL query arguments
   * @return Result of ResultSet mapping with processor
   * @param <T> Type of result
   */
  <T> T executeQuery(Connection connection, String query, QueryFunction<ResultSet, T> processor, Object... args) throws SQLException;

}
