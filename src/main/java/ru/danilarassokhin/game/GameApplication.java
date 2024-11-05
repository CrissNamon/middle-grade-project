package ru.danilarassokhin.game;

import ru.danilarassokhin.game.config.ApplicationConfig;
import ru.danilarassokhin.game.config.CamundaConfig;
import ru.danilarassokhin.game.config.DatabaseConfig;
import ru.danilarassokhin.game.config.HttpConfig;
import ru.danilarassokhin.game.server.GameServer;

public class GameApplication {

  public static void main(String[] args) {
    GameServer.start(ApplicationConfig.class, DatabaseConfig.class, CamundaConfig.class, HttpConfig.class);
  }

}
