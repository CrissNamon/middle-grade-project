package ru.danilarassokhin.game.service.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.sql.Connection;

/**
 * Marks method as update query.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Update {

  /**
   * Sql query.
   */
  String value();

  /**
   * Transaction isolation level.
   */
  int isolation() default Connection.TRANSACTION_READ_COMMITTED;

}
