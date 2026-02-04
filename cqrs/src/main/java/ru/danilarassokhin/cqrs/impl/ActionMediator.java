package ru.danilarassokhin.cqrs.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ru.danilarassokhin.cqrs.Action;
import ru.danilarassokhin.cqrs.ActionHandler;
import ru.danilarassokhin.cqrs.exception.HandlerException;

/**
 * Хранилище обработчиков для действий.
 */
public class ActionMediator {

  private final Map<Class<?>, ActionHandler> handlers = new ConcurrentHashMap<>();

  public ActionMediator(List<? extends ActionHandler> commandHandlers) {
    commandHandlers.forEach(handler -> {
      handlers.put(handler.getType(), handler);
    });
  }

  /**
   * Ищет обработчик и передает ему действие.
   * @param action {@link Action}
   * @return Результат выполнения действия
   */
  public <I, O> O execute(Action<I, O> action) {
    if (handlers.containsKey(action.getClass())) {
      return (O) handlers.get(action.getClass()).handle(action);
    }
    throw new HandlerException("Handler not found for: " + action.getClass());
  }

}
