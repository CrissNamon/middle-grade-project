package ru.danilarassokhin.game.util;

import java.util.List;
import java.util.Optional;
import java.util.Properties;

/**
 * Provides storage for application properties.
 */
public interface PropertiesFactory {

  /**
   * Gets property as int by name.
   *
   * @param name Name of property
   * @return Optional property value
   */
  Optional<Integer> getAsInt(String name);

  /**
   * Gets property as string by name.
   *
   * @param name Name of property
   * @return Optional property value
   */
  Optional<String> getAsString(String name);

  /**
   * @return {@link Properties}
   */
  Properties getAll();

  /**
   * Gets property as array with given delimiter.
   * @param name Name of property
   * @param delimiter Delimiter for values
   * @return Optional property values
   */
  Optional<List<String>> getAsArray(String name, String delimiter);
}
