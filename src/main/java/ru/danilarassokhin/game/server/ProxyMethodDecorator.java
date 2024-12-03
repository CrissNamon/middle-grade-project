package ru.danilarassokhin.game.server;

import java.lang.reflect.Method;

public interface ProxyMethodDecorator {

  ProxyMethod decorate(Object realObject, Method realMethod, ProxyMethod proxyMethod);

  boolean canBeDecorated(Object realObject, Method realMethod);

}
