package ru.danilarassokhin.game.server;

import java.util.Optional;

import ru.danilarassokhin.game.server.model.HttpRequestKey;

public interface DispatcherController {

  void addMapping(HttpRequestKey key, HttpRequestHandler mapping);

  Optional<HttpRequestHandler> findByKey(HttpRequestKey key);
}
