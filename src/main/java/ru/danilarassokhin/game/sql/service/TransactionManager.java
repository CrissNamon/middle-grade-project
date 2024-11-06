package ru.danilarassokhin.game.sql.service;

import java.sql.Connection;
import java.sql.ResultSet;

import ru.danilarassokhin.game.util.SneakyConsumer;
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
   * Executes query returning any results with default isolation level.
   * @param body Transaction body. See {@link TransactionTemplate}.
   * @return Result of query
   * @param <T> Type of result
   */
  <T> T fetchInTransaction(SneakyFunction<TransactionTemplate, T> body);

  /**
   * Executes query with no returning result with default isolation level.
   * @param body Transaction body. See {@link TransactionTemplate}.
   */
  void doInTransaction(SneakyConsumer<TransactionTemplate> body);

  /**
   * Executes query with no returning result with default isolation level.
   * @param isolationLevel Transaction isolation level
   * @param body Transaction body. See {@link TransactionTemplate}.
   */
  void doInTransaction(int isolationLevel, SneakyConsumer<TransactionTemplate> body);

  /**
   * Executes query returning any results.
   * @param isolationLevel Transaction isolation level
   * @param body Transaction body. See {@link TransactionTemplate}.
   * @return Result of query
   * @param <T> Type of result
   */
  <T> T fetchInTransaction(int isolationLevel, SneakyFunction<TransactionTemplate, T> body);

  /**
   * Executes update query with prepared statement.
   * @param connection {@link Connection} to use for query
   * @param query SQL query
   * @param args SQL query arguments
   * @return either (1) the row count for SQL Data Manipulation Language (DML) statements or (2) 0
   * for SQL statements that return nothing
   */
  int executeUpdate(Connection connection, String query, Object... args);

  /**
   * Executes query with prepared statement.
   * @param connection {@link Connection} to use for query
   * @param query SQL query
   * @param processor Mapper for query result
   * @param args SQL query arguments
   * @return Result of ResultSet mapping with processor
   * @param <T> Type of result
   */
  <T> T executeQuery(Connection connection, String query, SneakyFunction<ResultSet, T> processor, Object... args);
}
