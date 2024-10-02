package ru.danilarassokhin.game;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import ru.danilarassokhin.game.controller.TestController;
import ru.danilarassokhin.game.server.impl.ReflectiveDispatcherController;
import ru.danilarassokhin.game.server.netty.HttpServerInitializer;
import ru.danilarassokhin.game.server.netty.NettyServer;
import ru.danilarassokhin.game.server.reflection.impl.HttpBodyMapperImpl;
import ru.danilarassokhin.game.server.reflection.impl.HttpHandlerProcessorImpl;

public class GameApplication {

  public static void main(String[] args) {
    var testController = new TestController();
    var objectMapper = new ObjectMapper();
    var httpBodyMapper = new HttpBodyMapperImpl(objectMapper);
    var httpHandlerProcessor = new HttpHandlerProcessorImpl(httpBodyMapper);
    var dispatcherController = new ReflectiveDispatcherController(httpHandlerProcessor, testController);
    var loggingHandler = new LoggingHandler(LogLevel.DEBUG);
    var serverInitializer = new HttpServerInitializer(dispatcherController);
    var server = new NettyServer(8080, loggingHandler, serverInitializer);
    server.start();
  }

}
