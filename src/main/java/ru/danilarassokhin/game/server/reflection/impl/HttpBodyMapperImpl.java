package ru.danilarassokhin.game.server.reflection.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import ru.danilarassokhin.game.exception.HttpServerException;
import ru.danilarassokhin.game.server.model.HttpMediaType;
import ru.danilarassokhin.game.server.reflection.HttpBodyMapper;

/**
 * Maps response body from object to string and vice versa.
 */
@RequiredArgsConstructor
public class HttpBodyMapperImpl implements HttpBodyMapper {

  private final ObjectMapper objectMapper;

  /**
   * Serializes object to string according to content type.
   * @param contentType {@link ru.danilarassokhin.game.server.model.HttpMediaType}
   * @param body Response body
   * @return Serialized object
   */
  public String objectToString(String contentType, Object body) {
    try {
      switch (contentType) {
        case HttpMediaType.APPLICATION_JSON -> {
          return objectMapper.writeValueAsString(body);
        }
        default -> {
          return body.toString();
        }
      }
    } catch (Exception e) {
      throw new HttpServerException(e);
    }
  }

  /**
   * Deserializes string to object according to content type.
   * @param contentType {@link ru.danilarassokhin.game.server.model.HttpMediaType}
   * @param body Response body
   * @param bodyType Expected object class
   * @return Deserialized string
   */
  public Object stringToObject(String contentType, String body, Class<?> bodyType) {
    try {
      switch (contentType) {
        case HttpMediaType.APPLICATION_JSON -> {
          return objectMapper.readValue(body, bodyType);
        }
        default -> {
          return body;
        }
      }
    } catch (Exception e) {
      throw new HttpServerException(e);
    }
  }

}
