package ru.danilarassokhin.game.config;

import ru.danilarassokhin.game.util.PropertiesFactory;
import ru.danilarassokhin.game.util.impl.PropertiesFactoryImpl;
import tech.hiddenproject.progressive.annotation.ComponentScan;
import tech.hiddenproject.progressive.annotation.Configuration;
import tech.hiddenproject.progressive.annotation.GameBean;

@Configuration
@ComponentScan({"ru.danilarassokhin.game", "ru.danilarassokhin.game.service.impl"})
public class ApplicationConfig {

  public static final String SERVER_PORT_PROPERTY = "server.port";
  public static final String CAMUNDA_PROCESS_ID_PROPERTY = "app.camunda.process-id";
  public static final String DEFAULT_PROPERTY_DELIMITER = ";";

  @GameBean
  public PropertiesFactory propertiesFactory() {
    return new PropertiesFactoryImpl();
  }

}
