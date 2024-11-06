package ru.danilarassokhin.game.server.model;

import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * Response model for {@link ru.danilarassokhin.game.server.HttpRequestHandler}.
 *
 * @param status {@link HttpResponseStatus}
 * @param body Response body
 */
public record ResponseEntity(HttpResponseStatus status, Object body) {

  public static ResponseEntity ok(Object body) {
    return new ResponseEntity(HttpResponseStatus.OK, body);
  }

  public static ResponseEntity ok() {
    return ok(null);
  }

  public static ResponseEntity notFound() {
    return new ResponseEntity(HttpResponseStatus.NOT_FOUND, null);
  }

}
