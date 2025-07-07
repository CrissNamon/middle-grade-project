package ru.danilarassokhin.game.config;

import ru.danilarassokhin.game.controller.DungeonController;
import ru.danilarassokhin.game.controller.GlobalExceptionHandler;
import ru.danilarassokhin.game.controller.LotteryController;
import ru.danilarassokhin.game.controller.MarketController;
import ru.danilarassokhin.game.controller.PlayerController;
import ru.danilarassokhin.server.DispatcherController;
import ru.danilarassokhin.server.impl.ReflectiveDispatcherController;
import ru.danilarassokhin.server.reflection.HttpHandlerProcessor;
import tech.hiddenproject.progressive.annotation.Configuration;
import tech.hiddenproject.progressive.annotation.GameBean;

@Configuration
public class HttpConfig {

  @GameBean
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
