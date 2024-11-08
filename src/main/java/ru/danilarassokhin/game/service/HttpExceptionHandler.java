package ru.danilarassokhin.game.service;

import java.util.function.Function;

import ru.danilarassokhin.game.server.model.ResponseEntity;

public interface HttpExceptionHandler {

  @SuppressWarnings("unchecked")
  <T extends RuntimeException> void addHandler(Class<T> exceptionClass,
                                               Function<T, ResponseEntity> handler);

  ResponseEntity handle(RuntimeException exception);
}
