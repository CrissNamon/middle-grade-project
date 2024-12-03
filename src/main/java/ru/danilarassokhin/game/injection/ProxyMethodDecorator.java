package ru.danilarassokhin.game.injection;

import java.lang.reflect.Method;

/**
 * Decorated {@link ProxyMethod}.
 */
public interface ProxyMethodDecorator {

  /**
   * Decorates {@link ProxyMethod} with some logic.
   * @param realObject Real object
   * @param realMethod Real method
   * @param proxyMethod {@link ProxyMethod}
   * @return Decorated {@link ProxyMethod}
   */
  ProxyMethod decorate(Object realObject, Method realMethod, ProxyMethod proxyMethod);

  /**
   * Checks if real method can be decorated by this decorator.
   * @param realObject Real object
   * @param realMethod Real method
   * @return true if method can be decorated
   */
  boolean canBeDecorated(Object realObject, Method realMethod);

}
