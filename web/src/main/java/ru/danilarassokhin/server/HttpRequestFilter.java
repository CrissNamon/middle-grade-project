package ru.danilarassokhin.server;

import io.netty.handler.codec.http.FullHttpRequest;

/**
 * Accepts {@link FullHttpRequest} to check it. Can be used for security purposes.
 */
public interface HttpRequestFilter {

  /**
   * @param httpRequest {@link FullHttpRequest}
   */
  void filter(FullHttpRequest httpRequest);

}
