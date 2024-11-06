package ru.danilarassokhin.game.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import ru.danilarassokhin.game.server.model.ResponseEntity;
import tech.hiddenproject.progressive.annotation.GameBean;

@GameBean
public class HttpExceptionHandler {

  private final Map<Class<? extends RuntimeException>, Function<RuntimeException, ResponseEntity>> handlers = new HashMap<>();

  @SuppressWarnings("unchecked")
  public <T extends RuntimeException> void addHandler(Class<T> exceptionClass, Function<T, ResponseEntity> handler) {
    handlers.putIfAbsent(exceptionClass, e -> handler.apply((T)e));
  }

  public ResponseEntity handle(RuntimeException exception) {
    return handlers.getOrDefault(exception.getClass(), c -> ResponseEntity.internalError())
        .apply(exception);
  }

}
