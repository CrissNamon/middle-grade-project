package ru.danilarassokhin.server.exception;

import ru.danilarassokhin.server.netty.HttpServerHandler;

/**
 * Exceptions for authentication.
 * @see HttpServerHandler
 */
public class AuthenticationException extends RuntimeException {

  public AuthenticationException(Throwable cause) {
    super(cause);
  }
}
