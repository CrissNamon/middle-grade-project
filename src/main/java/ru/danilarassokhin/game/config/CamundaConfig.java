package ru.danilarassokhin.game.config;

import static ru.danilarassokhin.game.config.ApplicationConfig.PROPERTY_DEFAULT_DELIMITER;

import java.util.Collections;

import ru.danilarassokhin.game.service.BpmnService;
import ru.danilarassokhin.game.service.impl.BpmnServiceImpl;
import ru.danilarassokhin.game.util.PropertiesFactory;
import tech.hiddenproject.progressive.annotation.Configuration;
import tech.hiddenproject.progressive.annotation.GameBean;

@Configuration
public class CamundaConfig {

  public static final String CAMUNDA_DEPLOYMENTS_PROPERTY = "camunda.deployments";

  @GameBean
  public BpmnService bpmnService(PropertiesFactory propertiesFactory) {
    return new BpmnServiceImpl(
        propertiesFactory.getAsArray(CAMUNDA_DEPLOYMENTS_PROPERTY, PROPERTY_DEFAULT_DELIMITER)
            .orElse(Collections.emptyList())
    );
  }

}
