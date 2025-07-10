package ru.danilarassokhin.cqrs.exception;

public class HandlerException extends RuntimeException {

  public HandlerException(String message) {
    super(message);
  }
}
