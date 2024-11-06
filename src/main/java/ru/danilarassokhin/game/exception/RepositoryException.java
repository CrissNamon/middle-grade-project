package ru.danilarassokhin.game.exception;

/**
 * Exception for repository errors.
 */
public class RepositoryException extends RuntimeException {

  public RepositoryException(String message) {
    super(message);
  }
}
