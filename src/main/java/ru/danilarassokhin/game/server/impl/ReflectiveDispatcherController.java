package ru.danilarassokhin.game.server.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import io.netty.handler.codec.http.HttpMethod;
import ru.danilarassokhin.game.server.DispatcherController;
import ru.danilarassokhin.game.server.HttpRequestHandler;
import ru.danilarassokhin.game.server.annotation.GetRequest;
import ru.danilarassokhin.game.server.annotation.PostRequest;
import ru.danilarassokhin.game.server.model.HttpRequestKey;
import ru.danilarassokhin.game.server.model.Pair;
import ru.danilarassokhin.game.server.reflection.ReflectionUtil;
import tech.hiddenproject.aide.reflection.LambdaWrapperHolder;

public class ReflectiveDispatcherController implements DispatcherController {

  private final static Set<Class<? extends Annotation>> REQUEST_ANNOTATIONS = new HashSet<>() {{
    add(GetRequest.class);
    add(PostRequest.class);
  }};

  private final DefaultDispatcherController defaultDispatcherController = new DefaultDispatcherController();

  public ReflectiveDispatcherController(Object... controllers) {
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
    var wrapper = LambdaWrapperHolder.DEFAULT.wrap(method);
    var key = REQUEST_ANNOTATIONS.stream()
        .map(method::getAnnotation)
        .filter(Objects::nonNull)
        .findFirst()
        .map(this::annotationToHttpRequestKey)
        .orElseThrow();
    HttpRequestHandler handler = httpRequest -> wrapper.getWrapper().apply(controller, httpRequest);
    return Pair.of(key, handler);
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
}
