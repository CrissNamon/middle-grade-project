package ru.danilarassokhin.game.exception;

public class HttpServerException extends RuntimeException {

  public HttpServerException(String message) {
    super(message);
  }

  public HttpServerException(Throwable cause) {
    super(cause);
  }
}
