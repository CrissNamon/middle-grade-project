package ru.danilarassokhin.game.server;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

import lombok.RequiredArgsConstructor;
import ru.danilarassokhin.game.sql.annotation.Transactional;
import ru.danilarassokhin.game.sql.service.TransactionManager;
import ru.danilarassokhin.game.util.TypeUtils;
import tech.hiddenproject.aide.optional.ThrowableOptional;
import tech.hiddenproject.progressive.BasicComponentManager;
import tech.hiddenproject.progressive.ComponentCreator;
import tech.hiddenproject.progressive.annotation.GameBean;
import tech.hiddenproject.progressive.injection.DIContainer;

@RequiredArgsConstructor
public class BeanProxyCreator implements ComponentCreator {

  private static final ComponentCreator DEFAULT_CREATOR = BasicComponentManager.getComponentCreator();

  {
    BasicComponentManager.setComponentCreator(this);
  }

  private final DIContainer diContainer;
  private final List<ProxyMethodDecorator> proxyMethodDecoratorList;

  @Override
  public <C> C create(Class<C> componentClass, Object... args) {
    var realObject = DEFAULT_CREATOR.create(componentClass, args);
    if (componentClass.isAnnotationPresent(GameBean.class) && componentClass.getInterfaces().length > 0) {
      return TypeUtils.cast(createProxy(componentClass, realObject, diContainer));
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

  private Object createProxy(Class<?> beanClass, Object realObject, DIContainer diContainer) {
    return Proxy.newProxyInstance(beanClass.getClassLoader(), beanClass.getInterfaces(),
                                  new InvocationHandler() {
                                    @Override
                                    public Object invoke(Object proxy, Method method,
                                                         Object[] args) throws Throwable {
                                      System.out.println("INVOKING: " + method);
                                      var realMethod = realObject.getClass().getDeclaredMethod(method.getName(), method.getParameterTypes());
                                      ProxyMethod proxyMethod = new ProxyMethod() {
                                        @Override
                                        public Object invoke(Object invoker, Object... args) {
                                          return ThrowableOptional.sneaky(() -> realMethod.invoke(realObject, args));
                                        }
                                      };
                                      for (ProxyMethodDecorator proxyMethodDecorator : proxyMethodDecoratorList) {
                                        if (proxyMethodDecorator.canBeDecorated(realObject, realMethod)) {
                                          proxyMethod = proxyMethodDecorator.decorate(realObject, realMethod, proxyMethod);
                                        }
                                      }
                                      return proxyMethod.invoke(realObject, args);
                                    }
                                  });
  }
}
