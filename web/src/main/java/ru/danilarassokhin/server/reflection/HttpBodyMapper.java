package ru.danilarassokhin.server.reflection;

import ru.danilarassokhin.server.model.HttpMediaType;

/**
 * Mapper for HTTP requests.
 */
public interface HttpBodyMapper {

  /**
   * Serializes object according to HTTP content type.
   *
   * @param contentType {@link HttpMediaType}
   * @param body Response body
   * @return Serialized response body
   */
  String objectToString(String contentType, Object body);

  /**
   * Deserializes string to object according to HTTP content type.
   *
   * @param contentType {@link HttpMediaType}
   * @param body Request body
   * @param bodyType Object class
   * @return Deserialized request body
   */
  Object stringToObject(String contentType, String body, Class<?> bodyType);

}
