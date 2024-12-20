package ru.danilarassokhin.game.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validator;
import ru.danilarassokhin.game.controller.DungeonController;
import ru.danilarassokhin.game.controller.GlobalExceptionHandler;
import ru.danilarassokhin.game.controller.LotteryController;
import ru.danilarassokhin.game.controller.MarketController;
import ru.danilarassokhin.game.controller.PlayerController;
import ru.danilarassokhin.game.server.DispatcherController;
import ru.danilarassokhin.game.server.impl.ReflectiveDispatcherController;
import ru.danilarassokhin.game.server.reflection.HttpBodyMapper;
import ru.danilarassokhin.game.server.reflection.HttpHandlerProcessor;
import ru.danilarassokhin.game.server.reflection.impl.HttpBodyMapperImpl;
import ru.danilarassokhin.game.server.reflection.impl.HttpHandlerProcessorImpl;
import ru.danilarassokhin.game.service.HttpExceptionHandler;
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
  public HttpHandlerProcessor httpHandlerProcessor(
      HttpBodyMapper httpBodyMapper,
      Validator validator,
      HttpExceptionHandler httpExceptionHandler
  ) {
    return new HttpHandlerProcessorImpl(httpBodyMapper, validator, httpExceptionHandler);
  }

  @GameBean(order = 3)
  public DispatcherController dispatcherController(
      HttpHandlerProcessor httpHandlerProcessor,
      GlobalExceptionHandler globalExceptionHandler,
      PlayerController playerController,
      DungeonController dungeonController,
      MarketController marketController,
      LotteryController lotteryController
  ) {
    return new ReflectiveDispatcherController(httpHandlerProcessor, globalExceptionHandler,
                                              playerController, dungeonController, marketController,
                                              lotteryController);
  }

}
