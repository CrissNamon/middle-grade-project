package ru.danilarassokhin.game.factory;

import ru.danilarassokhin.game.model.resilience.BloomFilterWithPresence;

/**
 * Factory to create Bloom filters.
 */
public interface BloomFilterFactory {

  /**
   * Creates new or retrieves {@link BloomFilterWithPresence}.
   * @param name Name of this filter
   * @param elementsType Elements type
   * @return {@link BloomFilterWithPresence}
   * @param <T> Elements type
   */
  <T> BloomFilterWithPresence<T> create(String name, Class<T> elementsType);

}
