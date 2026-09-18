package ru.danilarassokhin.game.exception;

/**
 * Exception for Quartz scheduler errors.
 */
public class QuartzException extends RuntimeException {

  public QuartzException(Throwable cause) {
    super(cause);
  }

  public QuartzException(String message, Throwable cause) {
    super(message, cause);
  }
}
