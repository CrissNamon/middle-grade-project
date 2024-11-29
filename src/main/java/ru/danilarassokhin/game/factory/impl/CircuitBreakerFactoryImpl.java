package ru.danilarassokhin.game.factory.impl;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import lombok.extern.slf4j.Slf4j;
import ru.danilarassokhin.game.factory.CircuitBreakerFactory;
import tech.hiddenproject.progressive.annotation.GameBean;

/**
 * Implementation of {@link CircuitBreakerFactory}.
 */
@GameBean
@Slf4j
public class CircuitBreakerFactoryImpl implements CircuitBreakerFactory {

  private static final Integer DEFAULT_FAILURE_RATE_THRESHOLD = 50;
  private static final Integer DEFAULT_NUMBER_OF_CALLS_IN_HALF_OPEN = 2;
  private static final Duration DEFAULT_DURATION_OPEN_STATE = Duration.ofMillis(1000);

  private final Map<String, CircuitBreakerConfig> configs = new HashMap<>();
  private final Map<String, CircuitBreaker> circuitBreakers = new HashMap<>();

  @Override
  @SafeVarargs
  public final CircuitBreaker create(String name, Class<? extends Throwable>... recordExceptions) {
    return circuitBreakers.computeIfAbsent(name, circuitBreakerName -> {
        var config = configs.computeIfAbsent(name, configName -> createConfig(recordExceptions));
        var circuitBreaker = CircuitBreaker.of(name, config);
        circuitBreaker.getEventPublisher()
            .onCallNotPermitted(event -> log.error("CircuitBreaker call is not permitted: {}",
                                                   event.getCircuitBreakerName()));
        return circuitBreaker;
    });
  }

  @SafeVarargs
  private CircuitBreakerConfig createConfig(Class<? extends Throwable>... recordExceptions) {
    return CircuitBreakerConfig.custom()
        .failureRateThreshold(DEFAULT_FAILURE_RATE_THRESHOLD)
        .waitDurationInOpenState(DEFAULT_DURATION_OPEN_STATE)
        .permittedNumberOfCallsInHalfOpenState(DEFAULT_NUMBER_OF_CALLS_IN_HALF_OPEN)
        .recordExceptions(recordExceptions)
        .build();
  }
}
