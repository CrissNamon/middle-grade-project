package ru.danilarassokhin.game.controller;

import java.nio.charset.StandardCharsets;

import io.netty.handler.codec.http.FullHttpRequest;
import ru.danilarassokhin.game.server.annotation.GetRequest;
import ru.danilarassokhin.game.server.annotation.PostRequest;
import ru.danilarassokhin.game.server.model.ResponseEntity;

public class TestController {

  @GetRequest("/ping")
  public ResponseEntity ping(FullHttpRequest httpRequest) {
    System.out.println("REQUEST: " + httpRequest);
    return ResponseEntity.ok("pong");
  }

  @PostRequest("/echo")
  public ResponseEntity echo(FullHttpRequest httpRequest) {
    System.out.println("REQUEST: " + httpRequest);
    return ResponseEntity.ok(httpRequest.content().toString(StandardCharsets.UTF_8));
  }

}
