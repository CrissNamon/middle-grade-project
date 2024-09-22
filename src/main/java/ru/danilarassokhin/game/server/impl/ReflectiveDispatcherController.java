package ru.danilarassokhin.game.server.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import ru.danilarassokhin.game.server.DispatcherController;
import ru.danilarassokhin.game.server.HttpRequestHandler;
import ru.danilarassokhin.game.server.annotation.GetRequest;
import ru.danilarassokhin.game.server.annotation.PostRequest;
import ru.danilarassokhin.game.server.model.HttpRequestKey;
import ru.danilarassokhin.game.server.model.Pair;
import ru.danilarassokhin.game.server.model.ResponseEntity;
import ru.danilarassokhin.game.server.reflection.HttpRequestMapperWrapper;
import ru.danilarassokhin.game.server.reflection.ReflectionUtil;
import tech.hiddenproject.aide.reflection.LambdaWrapperHolder;

public class ReflectiveDispatcherController implements DispatcherController {

  private final static Set<Class<? extends Annotation>> REQUEST_ANNOTATIONS = new HashSet<>() {{
    add(GetRequest.class);
    add(PostRequest.class);
  }};

  private final DefaultDispatcherController defaultDispatcherController = new DefaultDispatcherController();
  private final ObjectMapper objectMapper;

  public ReflectiveDispatcherController(ObjectMapper objectMapper, Object... controllers) {
    LambdaWrapperHolder.EMPTY.add(HttpRequestMapperWrapper.class);
    this.objectMapper = objectMapper;
    Arrays.stream(controllers)
        .map(controller ->
                 Pair.of(controller, ReflectionUtil.findMethodsWithAnnotations(controller.getClass(),
                                                                               REQUEST_ANNOTATIONS)))
        .flatMap(controllerToMethods -> controllerToMethods.second().stream()
            .map(method -> methodToRequestHandler(controllerToMethods.first(), method)))
        .forEach(httpRequestMapping ->
                     addMapping(httpRequestMapping.first(), httpRequestMapping.second()));
  }

  @Override
  public void addMapping(HttpRequestKey key, HttpRequestHandler mapping) {
    findByKey(key).ifPresent(handler -> {
      throw new RuntimeException("Duplicate http request handler! Already contains: " + key);
    });
    defaultDispatcherController.addMapping(key, mapping);
  }

  @Override
  public Optional<HttpRequestHandler> findByKey(HttpRequestKey key) {
    return defaultDispatcherController.findByKey(key);
  }

  private Pair<HttpRequestKey, HttpRequestHandler> methodToRequestHandler(Object controller, Method method) {
    System.out.println("Found request mapper: " + method);
    var key = createHttpRequestKeyFromMethod(method);
    HttpRequestHandler handler = createHttpRequestHandlerFromMethod(controller, method);
    return Pair.of(key, handler);
  }

  private Object getMethodParameterFromHttpRequest(Method method, FullHttpRequest httpRequest) {
    var methodParameters = method.getParameterTypes();
    if (methodParameters.length != 1) {
      throw new RuntimeException("Wrong method parameters count: " + methodParameters.length);
    }
    try {
      var methodParameter = methodParameters[0];
      if (HttpRequest.class.isAssignableFrom(methodParameter)) {
        return httpRequest;
      } else {
        return objectMapper.readValue(
            httpRequest.content().toString(StandardCharsets.UTF_8),
            methodParameter
        );
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private HttpRequestKey createHttpRequestKeyFromMethod(Method method) {
    return REQUEST_ANNOTATIONS.stream()
        .map(method::getAnnotation)
        .filter(Objects::nonNull)
        .findFirst()
        .map(this::annotationToHttpRequestKey)
        .orElseThrow();
  }

  private HttpRequestKey annotationToHttpRequestKey(Annotation annotation) {
    switch (annotation) {
      case GetRequest getRequest -> {
        System.out.println("Found GET request mapper: " + annotation);
        return new HttpRequestKey(HttpMethod.GET, getRequest.value());
      }
      case PostRequest postRequest -> {
        System.out.println("Found POST request mapper: " + annotation);
        return new HttpRequestKey(HttpMethod.POST, postRequest.value());
      }
      default -> throw new RuntimeException("Unknown request mapper annotation");
    }
  }

  private HttpRequestHandler createHttpRequestHandlerFromMethod(Object controller, Method method) {
    var wrapper = LambdaWrapperHolder.EMPTY.wrap(method, HttpRequestMapperWrapper.class);
    return httpRequest -> {
      var methodParameterValue = getMethodParameterFromHttpRequest(method, httpRequest);
      if (method.getReturnType().equals(void.class)) {
        wrapper.getWrapper().voidRequest(controller, methodParameterValue);
        return ResponseEntity.ok();
      } else {
        return wrapper.getWrapper().request(controller, methodParameterValue);
      }
    };
  }

}
