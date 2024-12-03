package ru.danilarassokhin.game.injection;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

import lombok.RequiredArgsConstructor;
import ru.danilarassokhin.game.util.TypeUtils;
import tech.hiddenproject.aide.optional.ThrowableOptional;
import tech.hiddenproject.progressive.BasicComponentManager;
import tech.hiddenproject.progressive.ComponentCreator;
import tech.hiddenproject.progressive.annotation.GameBean;

/**
 * Implementation of {@link ComponentCreator} for proxy creation.
 */
@RequiredArgsConstructor
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
      var realMethod = realObject.getClass().getDeclaredMethod(method.getName(), method.getParameterTypes());
      var proxyMethod = createProxyMethod(realObject, realMethod);
      return decorate(proxyMethod, realObject, realMethod).invoke(realObject, args);
    };
    return Proxy.newProxyInstance(beanClass.getClassLoader(), beanClass.getInterfaces(), invocationHandler);
  }

  private ProxyMethod decorate(ProxyMethod initial, Object realObject, Method realMethod) {
    var proxyMethod = initial;
    for (ProxyMethodDecorator proxyMethodDecorator : proxyMethodDecoratorList) {
      if (proxyMethodDecorator.canBeDecorated(realObject, realMethod)) {
        proxyMethod = proxyMethodDecorator.decorate(realObject, realMethod, proxyMethod);
      }
    }
    return proxyMethod;
  }

  private ProxyMethod createProxyMethod(Object realObject, Method realMethod) {
    return (invoker, args) -> ThrowableOptional.sneaky(() -> realMethod.invoke(realObject, args));
  }
}
