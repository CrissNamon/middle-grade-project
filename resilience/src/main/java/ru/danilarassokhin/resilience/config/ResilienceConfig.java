package ru.danilarassokhin.resilience.config;

import ru.danilarassokhin.resilience.factory.BloomFilterFactory;
import ru.danilarassokhin.resilience.factory.impl.BloomFilterFactoryImpl;
import ru.danilarassokhin.resilience.factory.impl.CircuitBreakerFactoryImpl;
import tech.hiddenproject.progressive.annotation.GameBean;
import ru.danilarassokhin.resilience.factory.CircuitBreakerFactory;

public class ResilienceConfig {

  @GameBean
  public CircuitBreakerFactory circuitBreakerFactory() {
    return new CircuitBreakerFactoryImpl();
  }

  @GameBean
  public BloomFilterFactory bloomFilterFactory() {
    return new BloomFilterFactoryImpl();
  }

}
