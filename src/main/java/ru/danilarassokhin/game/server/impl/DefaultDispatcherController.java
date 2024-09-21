package ru.danilarassokhin.game.server.impl;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import ru.danilarassokhin.game.server.DispatcherController;
import ru.danilarassokhin.game.server.HttpRequestHandler;
import ru.danilarassokhin.game.server.model.HttpRequestKey;

public class DefaultDispatcherController implements DispatcherController {

  private final ConcurrentHashMap<HttpRequestKey, HttpRequestHandler> availableRequestMappings =
      new ConcurrentHashMap<>();

  @Override
  public void addMapping(HttpRequestKey key, HttpRequestHandler mapping) {
    availableRequestMappings.put(key, mapping);
  }

  @Override
  public Optional<HttpRequestHandler> findByKey(HttpRequestKey key) {
    return Optional.ofNullable(availableRequestMappings.get(key));
  }

}
