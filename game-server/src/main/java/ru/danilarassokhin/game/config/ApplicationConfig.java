package ru.danilarassokhin.game.config;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import ru.danilarassokhin.util.impl.PropertiesFactoryImpl;
import ru.danilarassokhin.util.PropertiesFactory;
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
