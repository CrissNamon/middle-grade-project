package ru.danilarassokhin.game;

import ru.danilarassokhin.game.server.netty.NettyServer;

public class GameApplication {

  public static void main(String[] args) {
    NettyServer.start();
  }

}
