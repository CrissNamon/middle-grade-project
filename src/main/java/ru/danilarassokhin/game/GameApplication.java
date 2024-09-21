package ru.danilarassokhin.game;

import java.nio.charset.StandardCharsets;

import io.netty.handler.codec.http.HttpResponseStatus;
import ru.danilarassokhin.game.server.model.ResponseEntity;
import ru.danilarassokhin.game.server.netty.NettyServer;

public class GameApplication {

  public static void main(String[] args) {
    var server = new NettyServer();
    server.get("/ping", httpRequest -> new ResponseEntity(HttpResponseStatus.OK, "pong"));
    server.post("/echo", httpRequest ->
        new ResponseEntity(HttpResponseStatus.OK, httpRequest.content().toString(StandardCharsets.UTF_8)));
    server.start();
  }

}
