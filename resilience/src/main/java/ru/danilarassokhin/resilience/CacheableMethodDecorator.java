package ru.danilarassokhin.resilience;

import javax.cache.Cache;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import ru.danilarassokhin.injection.ProxyMethod;
import ru.danilarassokhin.injection.ProxyMethodDecorator;
import ru.danilarassokhin.resilience.annotation.Cacheable;
import tech.hiddenproject.progressive.injection.DIContainer;

/**
 * {@link ProxyMethodDecorator} for {@link Cacheable}.
 */
@RequiredArgsConstructor
public class CacheableMethodDecorator implements ProxyMethodDecorator {

  private final DIContainer diContainer;

  @Override
  public ProxyMethod decorate(Object realObject, Method realMethod, ProxyMethod proxyMethod, Object... invokeArgs) {
    var cacheData = realMethod.getAnnotation(Cacheable.class);
    var cache = diContainer.getBean(cacheData.value(), Cache.class);
    var key = Objects.hash(invokeArgs);
    if (cache.containsKey(key)) {
      return (invoker, args) -> getFromCache(cache, key, realMethod.getReturnType());
    }
    return (invoker, args) -> {
      var result = proxyMethod.invoke(invoker, args);
      putInCache(cache, key, result);
      return result;
    };
  }

  public void putInCache(Cache cache, Integer key, Object value) {
    if (Objects.isNull(value)) {
      return;
    }
    switch (value) {
      case Optional o -> o.ifPresent(v -> cache.put(key, v));
      default -> cache.put(key, value);
    }
  }

  public Object getFromCache(Cache cache, Integer key, Class<?> returnType) {
    var cached = cache.get(key);
    if (returnType.equals(Optional.class)) {
      return Optional.ofNullable(cached);
    }
    return cached;
  }

  @Override
  public boolean canBeDecorated(Object realObject, Method realMethod) {
    return realMethod.isAnnotationPresent(Cacheable.class);
  }
}
