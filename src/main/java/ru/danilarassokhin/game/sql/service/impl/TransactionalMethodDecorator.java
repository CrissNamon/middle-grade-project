package ru.danilarassokhin.game.sql.service.impl;

import java.lang.reflect.Method;

import lombok.RequiredArgsConstructor;
import ru.danilarassokhin.game.server.ProxyMethod;
import ru.danilarassokhin.game.server.ProxyMethodDecorator;
import ru.danilarassokhin.game.sql.annotation.Transactional;
import ru.danilarassokhin.game.sql.service.TransactionManager;
import tech.hiddenproject.progressive.injection.DIContainer;

@RequiredArgsConstructor
public class TransactionalMethodDecorator implements ProxyMethodDecorator {

  private final DIContainer diContainer;

  @Override
  public ProxyMethod decorate(Object realObject, Method realMethod, ProxyMethod proxyMethod) {
    return (invoker, args) -> {
      var transactionManager = diContainer.getBean(TransactionManager.class);
      transactionManager.openTransaction();
      var result = proxyMethod.invoke(realObject, args);
      transactionManager.commit();
      return result;
    };
  }

  @Override
  public boolean canBeDecorated(Object realObject, Method realMethod) {
    return realMethod.isAnnotationPresent(Transactional.class);
  }
}
