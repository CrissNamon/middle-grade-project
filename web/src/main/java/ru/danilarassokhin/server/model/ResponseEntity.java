package ru.danilarassokhin.server.model;

import io.netty.handler.codec.http.HttpResponseStatus;
import ru.danilarassokhin.server.HttpRequestHandler;

/**
 * Response dto for {@link HttpRequestHandler}.
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

  public static ResponseEntity internalError() {
    return new ResponseEntity(HttpResponseStatus.INTERNAL_SERVER_ERROR, null);
  }

  public static ResponseEntity badRequest(Object body) {
    return new ResponseEntity(HttpResponseStatus.BAD_REQUEST, body);
  }

  public static ResponseEntity forbidden() {
    return new ResponseEntity(HttpResponseStatus.UNAUTHORIZED, null);
  }

}
