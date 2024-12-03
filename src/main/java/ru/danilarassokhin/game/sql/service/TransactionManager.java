package ru.danilarassokhin.game.sql.service;

import java.sql.Connection;

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
  <T> T startTransaction(QueryFunction<Connection, T> body);

  /**
   * Executes query returning any results with default isolation level.
   * @param body Transaction body. See {@link TransactionContext}.
   * @return Result of query
   * @param <T> Type of result
   */
  <T> T fetchInTransaction(QueryFunction<TransactionContext, T> body);

  /**
   * Executes query with no returning result with default isolation level.
   * @param body Transaction body. See {@link TransactionContext}.
   */
  void doInTransaction(QueryConsumer<TransactionContext> body);

  /**
   * Executes query with no returning result with default isolation level.
   * @param isolationLevel Transaction isolation level
   * @param body Transaction body. See {@link TransactionContext}.
   */
  void doInTransaction(int isolationLevel, QueryConsumer<TransactionContext> body);

  /**
   * Executes query returning any results.
   * @param isolationLevel Transaction isolation level
   * @param body Transaction body. See {@link TransactionContext}.
   * @return Result of query
   * @param <T> Type of result
   */
  <T> T fetchInTransaction(int isolationLevel, QueryFunction<TransactionContext, T> body);

  void commit();

  void openTransaction(int isolationLevel);

}
