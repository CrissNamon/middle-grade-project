package ru.danilarassokhin.game.server.impl;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import ru.danilarassokhin.game.server.DispatcherController;
import ru.danilarassokhin.game.server.HttpRequestHandler;
import ru.danilarassokhin.game.server.model.HttpRequestKey;
import ru.danilarassokhin.game.server.model.ResponseEntity;

public class DefaultDispatcherController implements DispatcherController {

  private final ConcurrentHashMap<HttpRequestKey, HttpRequestHandler> availableRequestMappings =
      new ConcurrentHashMap<>();

  public DefaultDispatcherController() {
    addMapping(new HttpRequestKey(HttpMethod.GET, "/hello"), httpRequest -> {
      return new ResponseEntity(HttpResponseStatus.OK, "Hi!");
    });
  }

  @Override
  public void addMapping(HttpRequestKey key, HttpRequestHandler mapping) {
    availableRequestMappings.put(key, mapping);
  }

  @Override
  public Optional<HttpRequestHandler> findByKey(HttpRequestKey key) {
    return Optional.ofNullable(availableRequestMappings.get(key));
  }

}
