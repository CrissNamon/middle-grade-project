package ru.danilarassokhin.game.server.impl;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import ru.danilarassokhin.game.server.DispatcherController;
import ru.danilarassokhin.game.server.HttpRequestHandler;
import ru.danilarassokhin.game.server.exception.HttpServerException;
import ru.danilarassokhin.game.server.model.HttpRequestKey;
import ru.danilarassokhin.game.server.model.HttpResponseEntity;
import ru.danilarassokhin.game.server.model.Pair;
import ru.danilarassokhin.game.server.reflection.HttpHandlerProcessor;

public class ReflectiveDispatcherController implements DispatcherController {

  private static final String DEFAULT_CONTENT_TYPE = HttpHeaderValues.TEXT_PLAIN.toString();
  private static final String HTTP_METHOD_NOT_ALLOWED_MESSAGE = "Method not allowed %s: %s";

  private final ConcurrentHashMap<HttpRequestKey, HttpRequestHandler> availableRequestMappings =
      new ConcurrentHashMap<>();

  public ReflectiveDispatcherController(HttpHandlerProcessor httpHandlerProcessor,
                                        Object... controllers) {
    Arrays.stream(controllers)
        .map(controller -> Pair.of(controller, controller.getClass().getDeclaredMethods()))
        .flatMap(controllerToMethods -> Arrays.stream(controllerToMethods.second())
            .map(method -> httpHandlerProcessor.methodToRequestHandler(controllerToMethods.first(),
                                                                       method)))
        .forEach(httpRequestMapping ->
                     addMapping(httpRequestMapping.first(), httpRequestMapping.second()));
  }

  @Override
  public void addMapping(HttpRequestKey key, HttpRequestHandler mapping) {
    findByKey(key).ifPresent(handler -> {
      throw new HttpServerException("Duplicate http request handler! Already contains: " + key);
    });
    availableRequestMappings.put(key, mapping);
  }

  @Override
  public Optional<HttpRequestHandler> findByKey(HttpRequestKey key) {
    return Optional.ofNullable(availableRequestMappings.get(key));
  }

  @Override
  public HttpResponseEntity handleRequest(FullHttpRequest httpRequest) {
    var optionalRequestHandler = findByKey(httpRequestToKey(httpRequest));
    return optionalRequestHandler.map(httpRequestHandler -> httpRequestHandler.handle(httpRequest))
        .orElseGet(() -> createMethodNotAllowedResponse(httpRequest));
  }

  private HttpRequestKey httpRequestToKey(HttpRequest httpRequest) {
    var contentType = getHeaderValue(httpRequest, HttpHeaderNames.CONTENT_TYPE.toString())
        .orElse(DEFAULT_CONTENT_TYPE);
    return new HttpRequestKey(httpRequest.method(), contentType, httpRequest.uri());
  }

  private HttpResponseEntity createMethodNotAllowedResponse(FullHttpRequest httpRequest) {
    return new HttpResponseEntity(
        getHeaderValue(httpRequest, HttpHeaderNames.CONTENT_TYPE.toString()).orElse(
            DEFAULT_CONTENT_TYPE),
        String.format(HTTP_METHOD_NOT_ALLOWED_MESSAGE, httpRequest.method().name(),
                      httpRequest.uri()),
        HttpResponseStatus.METHOD_NOT_ALLOWED
    );
  }

  private Optional<String> getHeaderValue(HttpRequest httpRequest, String name) {
    return Optional.ofNullable(httpRequest.headers().get(name));
  }
}