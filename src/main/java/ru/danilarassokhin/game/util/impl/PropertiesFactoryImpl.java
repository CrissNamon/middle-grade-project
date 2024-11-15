package ru.danilarassokhin.game.util.impl;

import java.util.Arrays;
import java.util.List;
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

  private static final String PROPERTIES_PATH = "/application.properties";

  private final Properties applicationProperties = new Properties();

  public PropertiesFactoryImpl() {
    try {
      log.info("Loading util properties from: {}", PROPERTIES_PATH);
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
    return Optional.ofNullable(applicationProperties.getProperty(name));
  }

  @Override
  public Properties getAll() {
    return applicationProperties;
  }

  @Override
  public Optional<List<String>> getAsArray(String name, String delimiter) {
    return Optional.ofNullable(applicationProperties.getProperty(name))
        .map(property -> Arrays.stream(property.split(delimiter)).toList());
  }
}
