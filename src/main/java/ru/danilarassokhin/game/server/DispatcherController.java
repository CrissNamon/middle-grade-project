package ru.danilarassokhin.game.server;

import java.util.Optional;

import io.netty.handler.codec.http.FullHttpRequest;
import org.apache.commons.lang3.tuple.ImmutablePair;
import ru.danilarassokhin.game.server.model.HttpRequestKey;
import ru.danilarassokhin.game.server.model.HttpResponseEntity;

/**
 * Accepts all http requests and dispatches them among handlers.
 */
public interface DispatcherController {

  /**
   * Adds new request handler.
   *
   * @param key {@link HttpRequestKey}
   * @param mapping {@link HttpRequestHandler}
   */
  void addMapping(HttpRequestKey key, HttpRequestHandler mapping);

  /**
   * Searches for handler by key.
   *
   * @param key {@link HttpRequestKey}
   * @return Optional {@link HttpRequestHandler}
   */
  Optional<ImmutablePair<HttpRequestKey, HttpRequestHandler>> findByKey(HttpRequestKey key);

  /**
   * Dispatches request to suitable handler.
   *
   * @param httpRequest {@link FullHttpRequest}
   * @return {@link HttpResponseEntity}
   */
  HttpResponseEntity handleRequest(FullHttpRequest httpRequest);
}
