package ru.danilarassokhin.game.server;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import ru.danilarassokhin.game.config.ApplicationConfig;
import ru.danilarassokhin.game.injection.BeanProxyCreator;
import ru.danilarassokhin.game.resilience.CircuitBreakerMethodDecorator;
import ru.danilarassokhin.game.security.LoggerHttpFilter;
import ru.danilarassokhin.game.server.netty.HttpServerInitializer;
import ru.danilarassokhin.game.server.netty.NettyServer;
import ru.danilarassokhin.game.sql.service.impl.TransactionalMethodDecorator;
import ru.danilarassokhin.game.util.PropertiesFactory;
import ru.danilarassokhin.game.util.PropertyNames;
import tech.hiddenproject.progressive.BasicComponentManager;
import tech.hiddenproject.progressive.basic.manager.BasicGamePublisher;

public class GameServer {

  private static final int DEFAULT_PORT = 8080;

  public static void start(Class<?>... configurations) {
    var diContainer = BasicComponentManager.getDiContainer();
    var transactionalMethodDecorator = new TransactionalMethodDecorator(diContainer);
    var circuitBreakerMethodDecorator = new CircuitBreakerMethodDecorator(diContainer);
    new BeanProxyCreator(List.of(circuitBreakerMethodDecorator, transactionalMethodDecorator));
    Arrays.stream(configurations).forEach(diContainer::loadConfiguration);
    var propertiesFactory = diContainer.getBean(PropertiesFactory.class);
    var dispatcherController = diContainer.getBean(DispatcherController.class);
    var loggingHandler = new LoggingHandler(LogLevel.DEBUG);
    var filterChain = Set.<HttpRequestFilter>of(new LoggerHttpFilter());
    var serverInitializer = new HttpServerInitializer(dispatcherController, filterChain);
    var port = propertiesFactory.getAsInt(PropertyNames.SERVER_PORT).orElse(DEFAULT_PORT);
    var server = new NettyServer(port, loggingHandler, serverInitializer);
    server.start();

    BasicGamePublisher.getInstance().sendTo(ApplicationConfig.SHUTDOWN_EVENT, null);
  }

}
