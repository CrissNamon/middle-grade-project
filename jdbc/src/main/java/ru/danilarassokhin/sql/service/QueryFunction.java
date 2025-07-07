package ru.danilarassokhin.sql.service;

import java.sql.SQLException;

/**
 * Function to use lambdas with checked exceptions without try-catch.
 * @param <I> Argument type
 * @param <R> Result type
 */
public interface QueryFunction<I, R> {

  R apply(I input) throws SQLException;

}
