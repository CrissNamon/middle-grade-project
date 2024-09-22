package ru.danilarassokhin.game.server.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import ru.danilarassokhin.game.server.model.HttpMediaType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface GetRequest {

  String value();
  String produces() default HttpMediaType.APPLICATION_JSON;
  String consumes() default HttpMediaType.APPLICATION_JSON;

}
