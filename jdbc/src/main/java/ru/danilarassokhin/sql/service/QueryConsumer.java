package ru.danilarassokhin.sql.service;

import java.sql.SQLException;

/**
 * Consumer to use lambdas with checked exceptions without try-catch.
 * @param <T> Argument type
 */
public interface QueryConsumer<T> {

  void accept(T argument) throws SQLException;

}
