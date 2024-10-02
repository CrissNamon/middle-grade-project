package ru.danilarassokhin.game.server.reflection.impl;

import static ru.danilarassokhin.game.server.model.HttpMediaType.TEXT_PLAIN;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.danilarassokhin.game.server.HttpRequestHandler;
import ru.danilarassokhin.game.server.HttpUtils;
import ru.danilarassokhin.game.server.annotation.GetRequest;
import ru.danilarassokhin.game.server.annotation.PostRequest;
import ru.danilarassokhin.game.exception.HttpServerException;
import ru.danilarassokhin.game.server.model.HttpRequestHandlerData;
import ru.danilarassokhin.game.server.model.HttpRequestKey;
import ru.danilarassokhin.game.server.model.HttpResponseEntity;
import ru.danilarassokhin.game.util.Pair;
import ru.danilarassokhin.game.server.model.ResponseEntity;
import ru.danilarassokhin.game.server.reflection.HttpBodyMapper;
import ru.danilarassokhin.game.server.reflection.HttpHandlerProcessor;
import ru.danilarassokhin.game.server.reflection.HttpRequestMapperWrapper;
import tech.hiddenproject.aide.reflection.LambdaWrapperHolder;

@RequiredArgsConstructor
@Slf4j
public class HttpHandlerProcessorImpl implements HttpHandlerProcessor {

  static {
    LambdaWrapperHolder.EMPTY.add(HttpRequestMapperWrapper.class);
  }

  private final static Set<Class<? extends Annotation>> REQUEST_ANNOTATIONS = new HashSet<>() {{
    add(GetRequest.class);
    add(PostRequest.class);
  }};

  private final HttpBodyMapper httpBodyMapper;

  public Pair<HttpRequestKey, HttpRequestHandler> methodToRequestHandler(Object controller, Method method) {
    log.debug("Found request mapper: {}", method);
    var handlerData = createHttpRequestHandlerDataFromMethod(method);
    HttpRequestHandler handler = createHttpRequestHandlerFromMethod(controller, method, handlerData);
    return Pair.of(handlerData.requestKey(), handler);
  }

  private HttpRequestHandlerData createHttpRequestHandlerDataFromMethod(Method method) {
    return REQUEST_ANNOTATIONS.stream()
        .map(method::getAnnotation)
        .filter(Objects::nonNull)
        .findFirst()
        .map(this::annotationToHttpRequestHandlerData)
        .orElseThrow();
  }

  private HttpRequestHandlerData annotationToHttpRequestHandlerData(Annotation annotation) {
    switch (annotation) {
      case GetRequest getRequest -> {
        log.debug("Found GET request mapper: {}", annotation);
        return new HttpRequestHandlerData(
            new HttpRequestKey(HttpMethod.GET, getRequest.consumes(), getRequest.value()),
            getRequest.consumes(),
            getRequest.produces()
        );
      }
      case PostRequest postRequest -> {
        log.debug("Found POST request mapper: {}", annotation);
        return new HttpRequestHandlerData(
            new HttpRequestKey(HttpMethod.POST, postRequest.consumes(), postRequest.value()),
            postRequest.consumes(),
            postRequest.produces()
        );
      }
      default -> throw new HttpServerException("Unknown request mapper annotation");
    }
  }

  private Object getMethodParameterFromHttpRequest(Method method, FullHttpRequest httpRequest) {
    var methodParameters = method.getParameterTypes();
    if (methodParameters.length != 1) {
      throw new HttpServerException("Wrong method parameters count: " + methodParameters.length);
    }
    try {
      var methodParameter = methodParameters[0];
      if (HttpRequest.class.isAssignableFrom(methodParameter)) {
        return httpRequest;
      } else {
        return httpBodyMapper.stringToObject(
            HttpUtils.getHeaderValue(httpRequest, HttpHeaderNames.CONTENT_TYPE).orElse(TEXT_PLAIN),
            httpRequest.content().toString(StandardCharsets.UTF_8),
            methodParameter
        );
      }
    } catch (Exception e) {
      throw new HttpServerException(e);
    }
  }

  private HttpRequestHandler createHttpRequestHandlerFromMethod(Object controller, Method method, HttpRequestHandlerData handlerData) {
    var wrapper = LambdaWrapperHolder.EMPTY.wrap(method, HttpRequestMapperWrapper.class);
    return httpRequest -> {
      var methodParameterValue = getMethodParameterFromHttpRequest(method, httpRequest);
      if (method.getReturnType().equals(void.class)) {
        wrapper.getWrapper().voidRequest(controller, methodParameterValue);
        return responseEntityToHttpResponse(handlerData, ResponseEntity.ok());
      } else {
        ResponseEntity result = wrapper.getWrapper().request(controller, methodParameterValue);
        return responseEntityToHttpResponse(handlerData, result);
      }
    };
  }

  private HttpResponseEntity responseEntityToHttpResponse(HttpRequestHandlerData handlerData, ResponseEntity responseEntity) {
    var mappedBody = httpBodyMapper.objectToString(handlerData.produces(), responseEntity.body());
    return new HttpResponseEntity(handlerData.produces(), mappedBody, responseEntity.status());
  }

}
