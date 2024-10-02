package ru.danilarassokhin.game;

import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import ru.danilarassokhin.game.security.TestHttpFilter;
import ru.danilarassokhin.game.server.HttpRequestFilter;
import ru.danilarassokhin.game.util.impl.PropertiesFactoryImpl;
import ru.danilarassokhin.game.util.PropertyNames;
import ru.danilarassokhin.game.controller.TestController;
import ru.danilarassokhin.game.server.impl.ReflectiveDispatcherController;
import ru.danilarassokhin.game.server.netty.HttpServerInitializer;
import ru.danilarassokhin.game.server.netty.NettyServer;
import ru.danilarassokhin.game.server.reflection.impl.HttpBodyMapperImpl;
import ru.danilarassokhin.game.server.reflection.impl.HttpHandlerProcessorImpl;

public class GameApplication {

  private static final int DEFAULT_PORT = 8080;

  public static void main(String[] args) {
    PropertiesFactoryImpl propertiesFactory = new PropertiesFactoryImpl();
    var testController = new TestController();
    var objectMapper = new ObjectMapper();
    var httpBodyMapper = new HttpBodyMapperImpl(objectMapper);
    var httpHandlerProcessor = new HttpHandlerProcessorImpl(httpBodyMapper);
    var dispatcherController = new ReflectiveDispatcherController(httpHandlerProcessor, testController);
    var loggingHandler = new LoggingHandler(LogLevel.DEBUG);
    var filterChain = Set.<HttpRequestFilter>of(new TestHttpFilter());
    var serverInitializer = new HttpServerInitializer(dispatcherController, filterChain);
    var port = propertiesFactory.getAsInt(PropertyNames.SERVER_PORT).orElse(DEFAULT_PORT);
    var server = new NettyServer(port, loggingHandler, serverInitializer);
    server.start();
  }

}
