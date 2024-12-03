package ru.danilarassokhin.game.sql.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.sql.Connection;

/**
 * Marks method as transactional.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Transactional {

  /**
   * Transaction isolation level.
   */
  int isolationLevel() default Connection.TRANSACTION_READ_COMMITTED;

}
