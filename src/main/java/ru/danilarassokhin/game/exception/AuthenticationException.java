package ru.danilarassokhin.game.exception;

/**
 * Exceptions for authentication.
 * @see ru.danilarassokhin.game.server.netty.HttpServerHandler
 */
public class AuthenticationException extends RuntimeException {

  public AuthenticationException(Throwable cause) {
    super(cause);
  }
}
