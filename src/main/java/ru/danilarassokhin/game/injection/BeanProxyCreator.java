package ru.danilarassokhin.game.injection;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Objects;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.danilarassokhin.game.exception.ApplicationException;
import ru.danilarassokhin.game.util.TypeUtils;
import tech.hiddenproject.progressive.BasicComponentManager;
import tech.hiddenproject.progressive.ComponentCreator;
import tech.hiddenproject.progressive.annotation.GameBean;

/**
 * Implementation of {@link ComponentCreator} for proxy creation.
 */
@RequiredArgsConstructor
@Slf4j
public class BeanProxyCreator implements ComponentCreator {

  private static final ComponentCreator DEFAULT_CREATOR = BasicComponentManager.getComponentCreator();

  {
    BasicComponentManager.setComponentCreator(this);
  }

  private final List<ProxyMethodDecorator> proxyMethodDecoratorList;

  @Override
  public <C> C create(Class<C> componentClass, Object... args) {
    var realObject = DEFAULT_CREATOR.create(componentClass, args);
    if (componentClass.isAnnotationPresent(GameBean.class) && componentClass.getInterfaces().length > 0) {
      return TypeUtils.cast(createProxy(componentClass, realObject));
    }
    return realObject;
  }

  @Override
  public Object[] injectBeansToParameters(Class<?> beanClass, Class<?>[] parameterTypes,
                                          Annotation[][] parameterAnnotations) {
    return DEFAULT_CREATOR.injectBeansToParameters(beanClass, parameterTypes, parameterAnnotations);
  }

  @Override
  public void setIsHandlesEnabled(boolean isHandlesEnabled) {
    DEFAULT_CREATOR.setIsHandlesEnabled(isHandlesEnabled);
  }

  private Object createProxy(Class<?> beanClass, Object realObject) {
    InvocationHandler invocationHandler = (proxy, method, args) -> {
      var realMethod = beanClass.getMethod(method.getName(), method.getParameterTypes());
      var proxyMethod = createProxyMethod(realObject, realMethod);
      return decorate(proxyMethod, realObject, realMethod, args).invoke(realObject, args);
    };
    return Proxy.newProxyInstance(beanClass.getClassLoader(), beanClass.getInterfaces(), invocationHandler);
  }

  private ProxyMethod decorate(ProxyMethod initial, Object realObject, Method realMethod, Object... args) {
    var proxyMethod = initial;
    for (ProxyMethodDecorator proxyMethodDecorator : proxyMethodDecoratorList) {
      if (proxyMethodDecorator.canBeDecorated(realObject, realMethod)) {
        proxyMethod = proxyMethodDecorator.decorate(realObject, realMethod, proxyMethod, args);
      }
    }
    return proxyMethod;
  }

  private ProxyMethod createProxyMethod(Object realObject, Method realMethod) {
    return (invoker, args) -> {
      try {
        return realMethod.invoke(realObject, args);
      } catch (IllegalAccessException e) {
        throw new ApplicationException(e);
      } catch (InvocationTargetException e) {
        if (Objects.nonNull(e.getCause()) && RuntimeException.class.isAssignableFrom(e.getCause().getClass())) {
          throw (RuntimeException) e.getCause();
        }
        throw new ApplicationException(e);
      }
    };
  }
}
