package ru.danilarassokhin.game.exception;

/**
 * Exceptions for Netty exceptions.
 */
public class HttpServerException extends RuntimeException {

  public HttpServerException(String message) {
    super(message);
  }

  public HttpServerException(Throwable cause) {
    super(cause);
  }
}
