package ru.danilarassokhin.server;

import java.util.function.Function;

import ru.danilarassokhin.server.model.ResponseEntity;

public interface HttpExceptionHandler {

  <T extends RuntimeException> void addHandler(Class<T> exceptionClass, Function<T, ResponseEntity> handler);

  ResponseEntity handle(RuntimeException exception);
}
