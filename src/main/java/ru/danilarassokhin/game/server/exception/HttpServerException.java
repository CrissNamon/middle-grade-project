package ru.danilarassokhin.game.server.exception;

public class HttpServerException extends RuntimeException {

  public HttpServerException(String message) {
    super(message);
  }

  public HttpServerException(Throwable cause) {
    super(cause);
  }
}
