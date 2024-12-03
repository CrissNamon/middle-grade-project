package ru.danilarassokhin.game.resilience;

import java.lang.reflect.Method;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.danilarassokhin.game.injection.ProxyMethod;
import ru.danilarassokhin.game.injection.ProxyMethodDecorator;
import ru.danilarassokhin.game.resilience.annotation.CircuitBreaker;
import tech.hiddenproject.progressive.injection.DIContainer;

@RequiredArgsConstructor
@Slf4j
public class CircuitBreakerMethodDecorator implements ProxyMethodDecorator {

  private final DIContainer diContainer;

  @Override
  public ProxyMethod decorate(Object realObject, Method realMethod, ProxyMethod proxyMethod) {
    var circuitBreakerData = realMethod.getAnnotation(CircuitBreaker.class);
    var circuitBreaker = diContainer.getBean(circuitBreakerData.value(), io.github.resilience4j.circuitbreaker.CircuitBreaker.class);
    log.info("Applying CircuitBreaker {} on {}", circuitBreakerData.value(), realMethod);
    return (invoker, args) -> circuitBreaker.executeSupplier(() -> proxyMethod.invoke(invoker, args));
  }

  @Override
  public boolean canBeDecorated(Object realObject, Method realMethod) {
    return realMethod.isAnnotationPresent(CircuitBreaker.class);
  }
}
