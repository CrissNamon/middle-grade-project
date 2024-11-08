package ru.danilarassokhin.game.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import lombok.extern.slf4j.Slf4j;
import ru.danilarassokhin.game.server.model.ResponseEntity;
import ru.danilarassokhin.game.service.HttpExceptionHandler;
import tech.hiddenproject.progressive.annotation.GameBean;

@GameBean
@Slf4j
public class HttpExceptionHandlerImpl implements HttpExceptionHandler {

  private final Map<Class<? extends RuntimeException>, Function<RuntimeException, ResponseEntity>> handlers = new HashMap<>();

  @SuppressWarnings("unchecked")
  @Override
  public <T extends RuntimeException> void addHandler(Class<T> exceptionClass,
                                                      Function<T, ResponseEntity> handler) {
    handlers.putIfAbsent(exceptionClass, e -> handler.apply((T)e));
  }

  @Override
  public ResponseEntity handle(RuntimeException exception) {
    log.error("Error", exception);
    return handlers.getOrDefault(exception.getClass(), e -> ResponseEntity.internalError()).apply(exception);
  }

}
