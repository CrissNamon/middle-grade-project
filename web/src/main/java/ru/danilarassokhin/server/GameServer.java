package ru.danilarassokhin.server;

import java.util.Set;

import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import ru.danilarassokhin.server.config.WebConfig;
import ru.danilarassokhin.server.netty.HttpServerInitializer;
import ru.danilarassokhin.server.netty.NettyServer;
import ru.danilarassokhin.util.PropertiesFactory;
import tech.hiddenproject.progressive.basic.manager.BasicGamePublisher;
import tech.hiddenproject.progressive.injection.DIContainer;

public class GameServer {

  private static final int DEFAULT_PORT = 8080;

  public static void start(DIContainer diContainer, Set<HttpRequestFilter> filterChain) {
    var propertiesFactory = diContainer.getBean(PropertiesFactory.class);
    var dispatcherController = diContainer.getBean(DispatcherController.class);
    var loggingHandler = new LoggingHandler(LogLevel.DEBUG);
    var serverInitializer = new HttpServerInitializer(dispatcherController, filterChain);
    var port = propertiesFactory.getAsInt(WebConfig.WEB_SERVER_PORT_PROPERTY_NAME).orElse(DEFAULT_PORT);
    var server = new NettyServer(port, loggingHandler, serverInitializer);
    server.start();

    BasicGamePublisher.getInstance().sendTo(WebConfig.WEB_SERVER_SHUTDOWN_EVENT_NAME, null);
  }

}
