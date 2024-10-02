package ru.danilarassokhin.game.server;

import io.netty.handler.codec.http.FullHttpRequest;

public interface HttpRequestFilter {

  void filter(FullHttpRequest httpRequest);

}
