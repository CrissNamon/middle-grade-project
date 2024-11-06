package ru.danilarassokhin.game.server.model;

import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * Response model for {@link ru.danilarassokhin.game.server.HttpRequestHandler}.
 *
 * @param status {@link HttpResponseStatus}
 * @param body Response body
 */
public record ResponseEntity(HttpResponseStatus status, String body) {

  public static ResponseEntity ok(String body) {
    return new ResponseEntity(HttpResponseStatus.OK, body);
  }

  public static ResponseEntity ok() {
    return ok(null);
  }

}
