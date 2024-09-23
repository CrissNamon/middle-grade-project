package ru.danilarassokhin.game.server;

import java.util.Optional;

import io.netty.handler.codec.http.FullHttpRequest;
import ru.danilarassokhin.game.server.model.HttpRequestKey;
import ru.danilarassokhin.game.server.model.HttpResponseEntity;

public interface DispatcherController {

  void addMapping(HttpRequestKey key, HttpRequestHandler mapping);

  Optional<HttpRequestHandler> findByKey(HttpRequestKey key);

  HttpResponseEntity handleRequest(FullHttpRequest httpRequest);
}
