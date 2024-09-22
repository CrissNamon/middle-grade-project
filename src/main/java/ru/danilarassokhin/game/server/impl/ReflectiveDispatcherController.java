package ru.danilarassokhin.game.server.impl;

import java.util.Arrays;
import java.util.Optional;

import ru.danilarassokhin.game.server.DispatcherController;
import ru.danilarassokhin.game.server.HttpRequestHandler;
import ru.danilarassokhin.game.server.model.HttpRequestKey;
import ru.danilarassokhin.game.server.model.Pair;
import ru.danilarassokhin.game.server.reflection.HttpHandlerProcessor;

public class ReflectiveDispatcherController implements DispatcherController {

  private final DefaultDispatcherController defaultDispatcherController = new DefaultDispatcherController();

  public ReflectiveDispatcherController(HttpHandlerProcessor httpHandlerProcessor, Object... controllers) {
    Arrays.stream(controllers)
        .map(controller -> Pair.of(controller, controller.getClass().getDeclaredMethods()))
        .flatMap(controllerToMethods -> Arrays.stream(controllerToMethods.second())
            .map(method -> httpHandlerProcessor.methodToRequestHandler(controllerToMethods.first(), method)))
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

}
