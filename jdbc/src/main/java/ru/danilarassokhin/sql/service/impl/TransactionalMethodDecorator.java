package ru.danilarassokhin.sql.service.impl;

import java.lang.reflect.Method;

import lombok.RequiredArgsConstructor;
import ru.danilarassokhin.injection.ProxyMethod;
import ru.danilarassokhin.injection.ProxyMethodDecorator;
import ru.danilarassokhin.sql.annotation.Transactional;
import ru.danilarassokhin.sql.service.TransactionManager;
import tech.hiddenproject.progressive.injection.DIContainer;

/**
 * {@link ProxyMethodDecorator} for {@link Transactional}.
 */
@RequiredArgsConstructor
public class TransactionalMethodDecorator implements ProxyMethodDecorator {

  private final DIContainer diContainer;

  @Override
  public ProxyMethod decorate(Object realObject, Method realMethod, ProxyMethod proxyMethod, Object... invokeArgs) {
    return (invoker, args) -> {
      var transactionManager = diContainer.getBean(TransactionManager.class);
      var transactionData = realMethod.getAnnotation(Transactional.class);
      return transactionManager.executeInTransaction(transactionData.isolationLevel(),
                                              () -> proxyMethod.invoke(realObject, args));
    };
  }

  @Override
  public boolean canBeDecorated(Object realObject, Method realMethod) {
    return realMethod.isAnnotationPresent(Transactional.class);
  }
}
