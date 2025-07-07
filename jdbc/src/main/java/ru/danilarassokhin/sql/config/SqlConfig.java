package ru.danilarassokhin.sql.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import ru.danilarassokhin.sql.exception.DataSourceConnectionException;
import ru.danilarassokhin.resilience.factory.CircuitBreakerFactory;
import tech.hiddenproject.progressive.annotation.ComponentScan;
import tech.hiddenproject.progressive.annotation.Configuration;
import tech.hiddenproject.progressive.annotation.GameBean;

@Configuration
@ComponentScan("ru.danilarassokhin.sql.service.impl")
public class SqlConfig {

  public static final String DATA_SOURCE_CIRCUIT_BREAKER_NAME = "dataSourceCircuitBreaker";

  @GameBean(name = DATA_SOURCE_CIRCUIT_BREAKER_NAME)
  public CircuitBreaker dataSourceCircuitBreaker(CircuitBreakerFactory circuitBreakerFactory) {
    return circuitBreakerFactory.create(DATA_SOURCE_CIRCUIT_BREAKER_NAME, DataSourceConnectionException.class);
  }

}
