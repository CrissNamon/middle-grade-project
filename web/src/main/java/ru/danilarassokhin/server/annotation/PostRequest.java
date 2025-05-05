package ru.danilarassokhin.server.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import ru.danilarassokhin.server.model.HttpMediaType;

/**
 * Annotation for POST requests.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PostRequest {

  /**
   * @return Request url.
   */
  String value();

  /**
   * @return Response media type.
   */
  String produces() default HttpMediaType.APPLICATION_JSON;

  /**
   * @return Request media types.
   */
  String consumes() default HttpMediaType.APPLICATION_JSON;

}
