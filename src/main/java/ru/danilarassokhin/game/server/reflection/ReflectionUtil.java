package ru.danilarassokhin.game.server.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class ReflectionUtil {

  public static List<Method> findMethodsWithAnnotations(
      Class<?> clazz,
      Set<Class<? extends Annotation>> annotations
  ) {
    return Arrays.stream(clazz.getMethods())
        .filter(method ->
              Arrays.stream(method.getDeclaredAnnotations())
              .map(Annotation::annotationType)
              .anyMatch(annotations::contains))
        .toList();
  }

}
