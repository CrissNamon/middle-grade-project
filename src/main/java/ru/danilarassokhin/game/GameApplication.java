package ru.danilarassokhin.game;

import ru.danilarassokhin.game.config.ApplicationConfig;
import ru.danilarassokhin.game.config.ComponentsConfig;
import ru.danilarassokhin.game.config.DataSourceConfig;
import ru.danilarassokhin.game.config.HttpConfig;
import ru.danilarassokhin.game.server.GameServer;

public class GameApplication {

  public static void main(String[] args) {
    GameServer.start(ApplicationConfig.class, ComponentsConfig.class, DataSourceConfig.class, HttpConfig.class);
  }

}
