package ru.danilarassokhin.game.model;

import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;
import ru.danilarassokhin.game.server.annotation.GetRequest;
import ru.danilarassokhin.game.server.annotation.PostRequest;
import ru.danilarassokhin.game.server.model.HttpMediaType;
import ru.danilarassokhin.game.server.model.ResponseEntity;

@Slf4j
public class TestController {

  @GetRequest("/ping")
  public ResponseEntity ping(FullHttpRequest httpRequest) {
    return ResponseEntity.ok("pong");
  }

  @PostRequest(value = "/echo", consumes = HttpMediaType.TEXT_PLAIN)
  public ResponseEntity echo(String body) {
    return ResponseEntity.ok(body);
  }

  @PostRequest("/dispose")
  public void dispose(FullHttpRequest httpRequest) {
    log.debug("Disposed: {}", httpRequest);
  }

}
