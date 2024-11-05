package ru.danilarassokhin.game.server.impl;

import static ru.danilarassokhin.game.server.model.HttpMediaType.TEXT_PLAIN;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import ru.danilarassokhin.game.server.DispatcherController;
import ru.danilarassokhin.game.server.HttpRequestHandler;
import ru.danilarassokhin.game.exception.HttpServerException;
import ru.danilarassokhin.game.util.HttpUtils;
import ru.danilarassokhin.game.server.model.HttpRequestKey;
import ru.danilarassokhin.game.server.model.HttpResponseEntity;
import ru.danilarassokhin.game.server.reflection.HttpHandlerProcessor;

/**
 * Controller for dispatching request among handlers.
 */
public class ReflectiveDispatcherController implements DispatcherController {

  private static final String HTTP_METHOD_NOT_ALLOWED_MESSAGE = "Method not allowed %s: %s";

  private final ConcurrentHashMap<HttpRequestKey, HttpRequestHandler> availableRequestMappings =
      new ConcurrentHashMap<>();

  private final HttpHandlerProcessor httpHandlerProcessor;

  /**
   * @param httpHandlerProcessor Processor for handlers.
   * @param controllers Http controllers classes.
   */
  public ReflectiveDispatcherController(HttpHandlerProcessor httpHandlerProcessor,
                                        Object... controllers) {
    this.httpHandlerProcessor = httpHandlerProcessor;
    Arrays.stream(controllers).forEach(this::extractMappings);
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

  private void extractMappings(Object controller) {
    Arrays.stream(controller.getClass().getDeclaredMethods())
        .map(method -> httpHandlerProcessor.methodToRequestHandler(controller, method))
        .filter(Objects::nonNull)
        .forEach(httpRequestMapping ->
                     addMapping(httpRequestMapping.getLeft(), httpRequestMapping.getRight()));
  }

  private HttpRequestKey httpRequestToKey(HttpRequest httpRequest) {
    var contentType = HttpUtils.getHeaderValue(httpRequest, HttpHeaderNames.CONTENT_TYPE)
        .orElse(TEXT_PLAIN);
    return new HttpRequestKey(httpRequest.method(), contentType, httpRequest.uri());
  }

  private HttpResponseEntity createMethodNotAllowedResponse(FullHttpRequest httpRequest) {
    return new HttpResponseEntity(
        HttpUtils.getHeaderValue(httpRequest, HttpHeaderNames.CONTENT_TYPE.toString()).orElse(TEXT_PLAIN),
        String.format(HTTP_METHOD_NOT_ALLOWED_MESSAGE, httpRequest.method().name(), httpRequest.uri()),
        HttpResponseStatus.METHOD_NOT_ALLOWED
    );
  }
}