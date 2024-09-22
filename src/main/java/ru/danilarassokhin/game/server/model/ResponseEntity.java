package ru.danilarassokhin.game.server.model;

import io.netty.handler.codec.http.HttpResponseStatus;

public record ResponseEntity(HttpResponseStatus status, String body) {

  public static ResponseEntity ok(String body) {
    return new ResponseEntity(HttpResponseStatus.OK, body);
  }

  public static ResponseEntity ok() {
    return ok(null);
  }

}
