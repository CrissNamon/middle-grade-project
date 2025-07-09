package ru.danilarassokhin.cqrs.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ru.danilarassokhin.cqrs.Action;
import ru.danilarassokhin.cqrs.ActionExecutor;
import ru.danilarassokhin.cqrs.ActionHandler;
import ru.danilarassokhin.cqrs.exception.HandlerException;

public class ActionExecutorImpl<T extends Action> implements ActionExecutor<T> {

  private final Map<Class<?>, ActionHandler> handlers = new ConcurrentHashMap<>();

  public ActionExecutorImpl(List<? extends ActionHandler> commandHandlers) {
    commandHandlers.forEach(handler -> {
      handlers.put(handler.getType(), handler);
    });
  }

  @Override
  public Object execute(Action action) {
    if (handlers.containsKey(action.getClass())) {
      return handlers.get(action.getClass()).handle(action);
    }
    throw new HandlerException("Handler not found for: " + action.getClass());
  }

}
