package ru.danilarassokhin.game.server;

import io.netty.handler.codec.http.FullHttpRequest;
import ru.danilarassokhin.game.server.model.HttpResponseEntity;
import ru.danilarassokhin.game.server.model.RequestEntity;

/**
 * Handles http request and returns result of it.
 */
public interface HttpRequestHandler {

  /**
   * @param httpRequest {@link FullHttpRequest}
   * @return {@link HttpResponseEntity}
   */
  HttpResponseEntity handle(RequestEntity httpRequest);

}
