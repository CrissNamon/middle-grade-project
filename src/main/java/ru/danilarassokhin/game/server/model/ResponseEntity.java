package ru.danilarassokhin.game.server.model;

import io.netty.handler.codec.http.HttpResponseStatus;

public record ResponseEntity(HttpResponseStatus status, Object body) {

  public static ResponseEntity ok(Object body) {
    return new ResponseEntity(HttpResponseStatus.OK, body);
  }

}
