package ru.danilarassokhin.resilience.factory;

import ru.danilarassokhin.resilience.model.BloomFilterWithPresence;

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
