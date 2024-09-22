package ru.danilarassokhin.game;

import ru.danilarassokhin.game.controller.TestController;
import ru.danilarassokhin.game.server.impl.ReflectiveDispatcherController;
import ru.danilarassokhin.game.server.netty.NettyServer;

public class GameApplication {

  public static void main(String[] args) {
    var testController = new TestController();
    var dispatcherController = new ReflectiveDispatcherController(testController);
    var server = new NettyServer(dispatcherController);
    server.start();
  }

}
