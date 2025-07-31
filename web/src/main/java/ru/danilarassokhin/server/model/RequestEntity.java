package ru.danilarassokhin.server.model;

import java.util.List;
import java.util.Map;

import io.netty.handler.codec.http.FullHttpRequest;
import ru.danilarassokhin.util.impl.TypeUtils;

public record RequestEntity(FullHttpRequest request, Map<String, Object> pathParameters, Map<String, List<String>> queryParameters) {

  public <T> T getPathParameter(String name) {
    return TypeUtils.cast(pathParameters.get(name));
  }

  public <T> T getQueryParameter(String name) {
    var values = queryParameters.get(name);
    if (values == null) {
      return null;
    }
    if (values.size() == 1) {
      return TypeUtils.cast(values.getFirst());
    }
    return TypeUtils.cast(values);
  }

}
