package ru.danilarassokhin.notification.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Добавляет {@link org.springframework.security.core.context.SecurityContext}
 * в возвращаемый {@link reactor.core.publisher.Mono}.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ReactiveAuthorized {

  /**
   * @return Spel выражение для получения JWT токена
   */
  String value();

}
