package ru.danilarassokhin.game.injection;

public interface ProxyMethod {

  Object invoke(Object invoker, Object... args);

}
