package ru.danilarassokhin.resilience.factory;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;

/**
 * Factory to create {@link CircuitBreaker}.
 */
public interface CircuitBreakerFactory {

  /**
   * Creates new or retrieves existing {@link CircuitBreaker}.
   * @param name Name of CircuitBreaker
   * @param recordExceptions Exceptions to record in CircuitBreaker as failures
   * @return {@link CircuitBreaker}
   */
  CircuitBreaker create(String name, Class<? extends Throwable>... recordExceptions);

}
