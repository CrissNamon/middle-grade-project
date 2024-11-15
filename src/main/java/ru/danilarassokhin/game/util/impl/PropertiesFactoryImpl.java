package ru.danilarassokhin.game.util.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;
import ru.danilarassokhin.game.util.PropertiesFactory;
import ru.danilarassokhin.game.exception.ApplicationException;

/**
 * Provides storage for application properties.
 * Properties will be read from resources/application.properties files.
 */
@Slf4j
public class PropertiesFactoryImpl implements PropertiesFactory {

  private static final String ENV_PROPERTIES_PREFIX = "${";
  private static final String ENV_PROPERTIES_POSTFIX = "}";
  private static final String PROPERTIES_PATH = "/application.properties";

  private final Properties applicationProperties = new Properties();
  private final Map<String, String> environmentProperties = new HashMap<>();

  public PropertiesFactoryImpl() {
    try {
      log.info("Loading util properties from: {}", PROPERTIES_PATH);
      System.getenv().forEach((property, value) -> environmentProperties.put(ENV_PROPERTIES_PREFIX + property + ENV_PROPERTIES_POSTFIX, value));
      applicationProperties.load(PropertiesFactoryImpl.class.getResourceAsStream(PROPERTIES_PATH));
    } catch (Exception e) {
      throw new ApplicationException("Failed to load properties file", e);
    }
  }

  @Override
  public Optional<Integer> getAsInt(String name) {
    return getAsString(name).map(Integer::valueOf);
  }

  @Override
  public Optional<String> getAsString(String name) {
    return Optional.ofNullable(applicationProperties.getProperty(name))
        .map(this::getValueFromEnv);
  }

  @Override
  public Properties getAll() {
    return applicationProperties;
  }

  private String getValueFromEnv(String value) {
    if (value.startsWith(ENV_PROPERTIES_PREFIX)) {
      return environmentProperties.get(ENV_PROPERTIES_PREFIX + value + ENV_PROPERTIES_POSTFIX);
    }
    return value;
  }

}
