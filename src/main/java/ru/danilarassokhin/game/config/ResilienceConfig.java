package ru.danilarassokhin.game.config;

import io.camunda.zeebe.client.api.command.ClientException;
import io.camunda.zeebe.client.api.command.ClientStatusException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import ru.danilarassokhin.game.exception.CamundaException;
import ru.danilarassokhin.game.exception.DataSourceConnectionException;
import ru.danilarassokhin.game.factory.CircuitBreakerFactory;
import ru.danilarassokhin.game.factory.impl.CircuitBreakerFactoryImpl;
import tech.hiddenproject.progressive.annotation.Configuration;
import tech.hiddenproject.progressive.annotation.GameBean;

@Configuration
public class ResilienceConfig {

  public static final String DATA_SOURCE_CIRCUIT_BREAKER_NAME = "dataSourceCircuitBreaker";
  public static final String CAMUNDA_CIRCUIT_BREAKER_NAME = "camundaCircuitBreaker";

  @GameBean
  public CircuitBreakerFactory circuitBreakerFactory() {
    return new CircuitBreakerFactoryImpl();
  }

  @GameBean(name = DATA_SOURCE_CIRCUIT_BREAKER_NAME)
  public CircuitBreaker dataSourceCircuitBreaker(CircuitBreakerFactory circuitBreakerFactory) {
    return circuitBreakerFactory.create(DATA_SOURCE_CIRCUIT_BREAKER_NAME, DataSourceConnectionException.class);
  }

  @GameBean(name = CAMUNDA_CIRCUIT_BREAKER_NAME)
  public CircuitBreaker camundaCircuitBreaker(CircuitBreakerFactory circuitBreakerFactory) {
    return circuitBreakerFactory.create(CAMUNDA_CIRCUIT_BREAKER_NAME, ClientStatusException.class,
                                        ClientException.class, CamundaException.class);
  }

}
