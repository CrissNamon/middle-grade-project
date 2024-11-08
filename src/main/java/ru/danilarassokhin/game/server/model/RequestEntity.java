package ru.danilarassokhin.game.server.model;

import java.util.Map;

import io.netty.handler.codec.http.FullHttpRequest;
import ru.danilarassokhin.game.util.TypeUtils;

public record RequestEntity(FullHttpRequest request, Map<String, Object> pathParameters) {

  public <T> T getPathParameter(String name) {
    return TypeUtils.cast(pathParameters.get(name));
  }

}
