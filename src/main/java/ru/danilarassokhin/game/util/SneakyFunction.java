package ru.danilarassokhin.game.util;

/**
 * Function to use lambdas with checked exceptions without try-catch.
 * @param <I> Argument type
 * @param <R> Result type
 */
public interface SneakyFunction<I, R> {

  R apply(I input) throws Throwable;

}
