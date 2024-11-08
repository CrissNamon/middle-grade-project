package ru.danilarassokhin.game.sql.service;

import java.util.List;
import java.util.Map;

/**
 * Represents context for query in transaction.
 */
public interface QueryContext {

  /**
   * Executes query.
   */
  void execute();

  /**
   * Executes query returning result.
   * @return Result of query in Map, where key is column name and value is column value
   */
  <T> List<T> fetchInto(Class<T> entityType);

  /**
   * Executes query returning only one result.
   * @return Result of query in Map, where key is column name and value is column value
   */
  <T> T fetchOne(Class<T> entityType);

  /**
   * Executes query returning raw result.
   * @return Result of query in Map, where key is column name and value is column value
   */
  <T> List<Map<String, T>> fetchRaw();

  /**
   * Executes query returning one raw result.
   * @return Result of query in Map, where key is column name and value is column value
   */
  <T> Map<String, T> fetchOneRaw();
}
