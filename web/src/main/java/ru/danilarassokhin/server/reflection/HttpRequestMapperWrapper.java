package ru.danilarassokhin.server.reflection;

import tech.hiddenproject.aide.reflection.annotation.Invoker;

/**
 * Wrapper for reflective method calls.
 * @see tech.hiddenproject.aide.reflection.LambdaWrapper
 */
public interface HttpRequestMapperWrapper {

  @Invoker
  void voidRequest(Object caller, Object arg0);

  @Invoker
  <T> T request(Object caller, Object arg0);

}
