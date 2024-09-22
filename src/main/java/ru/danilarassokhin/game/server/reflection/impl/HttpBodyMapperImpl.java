package ru.danilarassokhin.game.server.reflection.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.danilarassokhin.game.server.model.HttpMediaType;
import ru.danilarassokhin.game.server.reflection.HttpBodyMapper;

public class HttpBodyMapperImpl implements HttpBodyMapper {

  private final ObjectMapper objectMapper;

  public HttpBodyMapperImpl(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

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
      throw new RuntimeException(e);
    }
  }

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
      throw new RuntimeException(e);
    }
  }

}
