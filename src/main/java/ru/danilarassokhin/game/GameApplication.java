package ru.danilarassokhin.game;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.danilarassokhin.game.controller.TestController;
import ru.danilarassokhin.game.server.impl.ReflectiveDispatcherController;
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
    var server = new NettyServer(dispatcherController);
    server.start();
  }

}
