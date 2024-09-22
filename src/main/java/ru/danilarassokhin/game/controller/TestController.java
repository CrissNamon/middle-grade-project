package ru.danilarassokhin.game.controller;

import io.netty.handler.codec.http.FullHttpRequest;
import ru.danilarassokhin.game.server.annotation.GetRequest;
import ru.danilarassokhin.game.server.annotation.PostRequest;
import ru.danilarassokhin.game.server.annotation.RequestBody;
import ru.danilarassokhin.game.server.model.ResponseEntity;

public class TestController {

  @GetRequest("/ping")
  public ResponseEntity ping(FullHttpRequest httpRequest) {
    System.out.println("REQUEST: " + httpRequest);
    return ResponseEntity.ok("pong");
  }

  @PostRequest("/echo")
  public ResponseEntity echo(@RequestBody String body) {
    System.out.println("REQUEST: " + body);
    return ResponseEntity.ok(body);
  }

  @PostRequest("/dispose")
  public void dispose(FullHttpRequest httpRequest) {
    System.out.println("DISPOSE REQUEST: " + httpRequest);
  }

}
