package ru.danilarassokhin.game.server;

public interface ProxyMethod {

  Object invoke(Object invoker, Object... args);

}
