package ru.danilarassokhin.game.config;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import ru.danilarassokhin.game.util.PropertiesFactory;
import ru.danilarassokhin.game.util.impl.PropertiesFactoryImpl;
import tech.hiddenproject.progressive.annotation.Configuration;
import tech.hiddenproject.progressive.annotation.GameBean;

@Configuration
public class ApplicationConfig {

  @GameBean
  public PropertiesFactory propertiesFactory() {
    return new PropertiesFactoryImpl();
  }

  @GameBean
  public Validator validator() {
    return Validation.buildDefaultValidatorFactory().getValidator();
  }

}
