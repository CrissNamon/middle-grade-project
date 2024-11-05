package ru.danilarassokhin.game.service.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks interface as repository.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface GameRepository {

  /**
   * Entity type.
   * @see Entity
   */
  Class<?> value();

  /**
   * Repository name.
   */
  String name() default "";

}
