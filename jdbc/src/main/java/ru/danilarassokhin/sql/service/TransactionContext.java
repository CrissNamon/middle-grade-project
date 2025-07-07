package ru.danilarassokhin.sql.service;

import java.sql.Connection;

/**
 * Represents transaction context.
 */
public interface TransactionContext {

  /**
   * Sets default schema for transaction.
   * @param schemaName SQL schema name
   */
  void useSchema(String schemaName);

  /**
   * Marks transaction as read-only.
   */
  void readOnly();

  /**
   * Creates new query in current transaction.
   * @param query SQL query
   * @param args {@link java.sql.PreparedStatement} arguments
   * @return {@link QueryContext}
   */
  QueryContext query(String query, Object... args);

  /**
   * Creates raw query in current transaction.
   * @param mapper Action on {@link Connection}
   * @return Result of query
   * @param <T> Type of result
   */
  <T> T rawQuery(QueryFunction<Connection, T> mapper);
}
