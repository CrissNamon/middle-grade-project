package ru.danilarassokhin.game.server;

import java.util.Arrays;
import java.util.Set;

import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import ru.danilarassokhin.game.security.LoggerHttpFilter;
import ru.danilarassokhin.game.server.netty.HttpServerInitializer;
import ru.danilarassokhin.game.server.netty.NettyServer;
import ru.danilarassokhin.game.util.PropertiesFactory;
import ru.danilarassokhin.game.util.PropertyNames;
import tech.hiddenproject.progressive.BasicComponentManager;

public class GameServer {

  private static final int DEFAULT_PORT = 8080;

  public static void start(Class<?>... configurations) {
    var diContainer = BasicComponentManager.getDiContainer();
    Arrays.stream(configurations).forEach(diContainer::loadConfiguration);

    var propertiesFactory = diContainer.getBean(PropertiesFactory.class);
    var dispatcherController = diContainer.getBean(DispatcherController.class);
    var loggingHandler = new LoggingHandler(LogLevel.DEBUG);
    var filterChain = Set.<HttpRequestFilter>of(new LoggerHttpFilter());
    var serverInitializer = new HttpServerInitializer(dispatcherController, filterChain);
    var port = propertiesFactory.getAsInt(PropertyNames.SERVER_PORT).orElse(DEFAULT_PORT);
    var server = new NettyServer(port, loggingHandler, serverInitializer);
    server.start();
  }

}
