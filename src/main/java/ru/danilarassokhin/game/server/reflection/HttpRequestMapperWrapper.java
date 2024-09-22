package ru.danilarassokhin.game.server.reflection;

import tech.hiddenproject.aide.reflection.annotation.Invoker;

public interface HttpRequestMapperWrapper {

  @Invoker
  void voidRequest(Object caller, Object arg0);

  @Invoker
  <T> T request(Object caller, Object arg0);

}
