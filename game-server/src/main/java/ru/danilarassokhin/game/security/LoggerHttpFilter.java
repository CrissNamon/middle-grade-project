package ru.danilarassokhin.game.security;

import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;
import ru.danilarassokhin.server.HttpRequestFilter;

@Slf4j
public class LoggerHttpFilter implements HttpRequestFilter {

  @Override
  public void filter(FullHttpRequest httpRequest) {
    log.debug("Request passed filter: {}", httpRequest);
  }
}
