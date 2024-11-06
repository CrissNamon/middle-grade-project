package ru.danilarassokhin.game.sql.service;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

/**
 * Template for transaction.
 */
public interface TransactionTemplate {

  /**
   * Executes query returning raw result.
   * @param query SQL query
   * @param args Arguments for prepared statement
   * @return Result of query in Map, where key is column name and value is column value
   * @param <T> Type of column values
   */
  <T> List<Map<String, T>> fetchRaw(String query, Object... args);

  /**
   * Executes query returning one raw result.
   * @param query SQL query
   * @param args Arguments for prepared statement
   * @return Result of query in Map, where key is column name and value is column value
   * @param <T> Type of column values
   */
  <T> Map<String, T> fetchOneRaw(String query, Object... args);

  /**
   * Executes query returning result.
   * @param query SQL query
   * @param args Arguments for prepared statement
   * @param type Java type to map result
   * @return Result of query in Map, where key is column name and value is column value
   * @param <T> Java type for table
   */
  <T> List<T> fetch(String query, Class<T> type, Object... args);

  /**
   * Executes query returning only one result.
   * @param query SQL query
   * @param args Arguments for prepared statement
   * @param type Java type to map result
   * @return Result of query in Map, where key is column name and value is column value
   * @param <T> Java type for table
   */
  <T> T fetchOne(String query, Class<T> type, Object... args);

  /**
   * Executes update query.
   * @param query SQL query
   * @param args Arguments for prepared statement
   * @return either (1) the row count for SQL Data Manipulation Language (DML) statements
   * or (2) 0 for SQL statements that return nothing
   */
  int executeUpdate(String query, Object... args);

  void useSchema(String schemaName);

  /**
   * Returns {@link Connection} associate with this transaction.
   * @return {@link Connection}
   */
  Connection getConnection();
}
