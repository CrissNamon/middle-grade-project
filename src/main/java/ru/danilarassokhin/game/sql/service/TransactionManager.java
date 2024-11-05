package ru.danilarassokhin.game.sql.service;

import java.sql.Connection;
import java.sql.ResultSet;

import ru.danilarassokhin.game.util.SneakyFunction;

/**
 * Service to work with JDBC transactions.
 */
public interface TransactionManager {

  /**
   * Starts new transaction.
   * @param body Transaction body
   * @return Result of transaction execution.
   * @param <T> Type of result
   */
  <T> T startTransaction(SneakyFunction<Connection, T> body);

  /**
   * Executes update query with prepared statement.
   * @param isolationLevel Transaction isolation level
   * @param query SQL query
   * @param args SQL query arguments
   * @return either (1) the row count for SQL Data Manipulation Language (DML) statements or (2) 0
   * for SQL statements that return nothing
   */
  int executeUpdate(int isolationLevel, String query, Object... args);

  /**
   * Executes query with prepared statement.
   * @param query SQL query
   * @param processor Mapper for query result
   * @param args SQL query arguments
   * @return Result of ResultSet mapping with processor
   * @param <T> Type of result
   */
  <T> T executeQuery(String query, SneakyFunction<ResultSet, T> processor, Object... args);
}
