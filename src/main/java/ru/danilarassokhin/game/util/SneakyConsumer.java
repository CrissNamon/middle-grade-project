package ru.danilarassokhin.game.util;

/**
 * Consumer to use lambdas with checked exceptions without try-catch.
 * @param <T> Argument type
 */
public interface SneakyConsumer<T> {

  void accept(T argument) throws Exception;

}
