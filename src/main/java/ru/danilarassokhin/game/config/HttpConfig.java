package ru.danilarassokhin.game.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.danilarassokhin.game.controller.PlayerController;
import ru.danilarassokhin.game.server.DispatcherController;
import ru.danilarassokhin.game.server.impl.ReflectiveDispatcherController;
import ru.danilarassokhin.game.server.reflection.HttpBodyMapper;
import ru.danilarassokhin.game.server.reflection.HttpHandlerProcessor;
import ru.danilarassokhin.game.server.reflection.impl.HttpBodyMapperImpl;
import ru.danilarassokhin.game.server.reflection.impl.HttpHandlerProcessorImpl;
import tech.hiddenproject.progressive.annotation.Configuration;
import tech.hiddenproject.progressive.annotation.GameBean;

@Configuration
public class HttpConfig {

  @GameBean
  public ObjectMapper objectMapper() {
    return new ObjectMapper();
  }

  @GameBean(order = 1)
  public HttpBodyMapper httpBodyMapper(ObjectMapper objectMapper) {
    return new HttpBodyMapperImpl(objectMapper);
  }

  @GameBean(order = 2)
  public HttpHandlerProcessor httpHandlerProcessor(HttpBodyMapper httpBodyMapper) {
    return new HttpHandlerProcessorImpl(httpBodyMapper);
  }

  @GameBean(order = 3)
  public DispatcherController dispatcherController(HttpHandlerProcessor httpHandlerProcessor, PlayerController playerController) {
    return new ReflectiveDispatcherController(httpHandlerProcessor, playerController);
  }

}
