package ru.danilarassokhin.game.exception;

public class AuthException extends RuntimeException {

  public AuthException(String message) {
    super(message);
  }

  public AuthException(Throwable cause) {
    super(cause);
  }
}
