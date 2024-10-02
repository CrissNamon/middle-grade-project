package ru.danilarassokhin.game.util;

import java.util.Optional;

public interface PropertiesFactory {

  Optional<Integer> getAsInt(String name);

  Optional<String> getAsString(String name);
}
