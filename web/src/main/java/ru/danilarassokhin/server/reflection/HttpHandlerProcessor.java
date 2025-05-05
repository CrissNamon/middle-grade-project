package ru.danilarassokhin.server.reflection;

import java.lang.reflect.Method;

import org.apache.commons.lang3.tuple.ImmutablePair;
import ru.danilarassokhin.server.HttpRequestHandler;
import ru.danilarassokhin.server.model.HttpRequestKey;

/**
 * Processes impl classes.
 */
public interface HttpHandlerProcessor {

  /**
   * Extracts request handlers from impl classes.
   */
  ImmutablePair<HttpRequestKey, HttpRequestHandler> methodToRequestHandler(Object controller, Method method);

}
