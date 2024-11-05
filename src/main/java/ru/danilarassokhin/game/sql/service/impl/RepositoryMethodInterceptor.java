package ru.danilarassokhin.game.sql.service.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Objects;

import lombok.RequiredArgsConstructor;
import ru.danilarassokhin.game.exception.RepositoryException;
import ru.danilarassokhin.game.sql.annotation.Entity;
import ru.danilarassokhin.game.sql.annotation.Select;
import ru.danilarassokhin.game.sql.annotation.Update;

/**
 * {@link InvocationHandler} for repository proxy.
 */
@RequiredArgsConstructor
public class RepositoryMethodInterceptor implements InvocationHandler {

  private static final String TABLE_PLACEHOLDER = ":table:";

  private final DefaultRepository defaultRepository;

  @Override
  public Object invoke(Object proxy, Method originMethod, Object[] args) throws Throwable {
    var arguments = processArguments(args);
    var entityType = defaultRepository.getEntityType();
    if (originMethod.isDefault()) {
      return InvocationHandler.invokeDefault(proxy, originMethod, arguments);
    }
    if (originMethod.isAnnotationPresent(Select.class)) {
      return processSelectMethod(entityType, originMethod, arguments);
    }
    if (originMethod.isAnnotationPresent(Update.class)) {
      return processUpdateMethod(entityType, originMethod, arguments);
    }
    throw new RepositoryException("Method " + originMethod + " could not be processed!");
  }

  private Object[] processArguments(Object[] args) {
    if (Objects.isNull(args)) {
      return new Object[0];
    }
    return args;
  }

  private String processQueryString(String query, Class<?> entityType) {
    var table = entityType.getAnnotation(Entity.class).value();
    return query.replaceAll(TABLE_PLACEHOLDER, table);
  }

  private Object processSelectMethod(Class<?> entityType, Method originMethod, Object[] args) {
    var resultType = (!Collection.class.isAssignableFrom(originMethod.getReturnType()) && !originMethod.getReturnType().equals(entityType)) ? originMethod.getReturnType() : entityType;
    var select = originMethod.getAnnotation(Select.class);
    var query = processQueryString(select.value(), entityType);
    var result = defaultRepository.executeQuery(resultType, query, select.rawResult(), args);
    if (originMethod.getReturnType().equals(void.class)) {
      return null;
    }
    if (Collection.class.isAssignableFrom(originMethod.getReturnType())) {
      return result;
    }
    if (result.size() > 1 && !Collection.class.isAssignableFrom(originMethod.getReturnType())) {
      throw new RepositoryException("Could not process result of method " + originMethod);
    }
    if (result.size() == 1 && !Collection.class.isAssignableFrom(originMethod.getReturnType())) {
      return result.getFirst();
    }
    if (result.isEmpty() && !Collection.class.isAssignableFrom(originMethod.getReturnType())) {
      return null;
    }
    throw new RepositoryException("Could not process result of method " + originMethod);
  }

  private Object processUpdateMethod(Class<?> entityType, Method originMethod, Object[] args) {
    var update = originMethod.getAnnotation(Update.class);
    var result = defaultRepository.executeUpdate(processQueryString(update.value(), entityType), update.isolation(), args);
    if (originMethod.getReturnType().equals(void.class)) {
      return null;
    }
    if (originMethod.getReturnType().isAssignableFrom(Integer.class) || originMethod.getReturnType().isAssignableFrom(int.class)) {
      return result;
    }
    throw new RepositoryException("Could not process result of method " + originMethod);
  }
}
