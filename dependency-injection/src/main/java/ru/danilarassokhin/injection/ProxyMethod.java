package ru.danilarassokhin.injection;

/**
 * Represents proxy method.
 */
public interface ProxyMethod {

  /**
   * Invokes proxy method.
   * @param invoker Object to invoke from.
   * @param args Method arguments
   * @return Invocation result
   */
  Object invoke(Object invoker, Object... args);

}
