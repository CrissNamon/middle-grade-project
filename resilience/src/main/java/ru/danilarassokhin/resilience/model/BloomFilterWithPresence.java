package ru.danilarassokhin.resilience.model;

import com.google.common.hash.BloomFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Uses 2 bloom filters to check if value was ever put in the filter and if it is present in the filter.
 * @param <T> Elements type.
 */
@RequiredArgsConstructor
@Slf4j
public class BloomFilterWithPresence<T> {

  private final BloomFilter<T> contentFilter;
  private final BloomFilter<T> presenceFilter;

  /**
   * Firstly checks presence filter. If result is false, then value has never been put in the filter.
   * Secondly checks content filter. If result is false, then value is definitely not in the filter.
   * Otherwise, filter might contain the value.
   * @param object Object to check
   * @return {@link BloomFilterResult}
   */
  public BloomFilterResult mightContain(T object) {
    if (!presenceFilter.mightContain(object)) {
      return BloomFilterResult.UNKNOWN;
    }
    if (!contentFilter.mightContain(object)) {
      return BloomFilterResult.NOT_CONTAINS;
    }
    return BloomFilterResult.MIGHT_CONTAINS;
  }

  /**
   * Acknowledges object without putting into filter.
   * @param object Object to acknowledge
   */
  public void acknowledge(T object) {
    presenceFilter.put(object);
  }

  /**
   * Puts value into the filter.
   * @param object Object to put
   */
  public void put(T object) {
    acknowledge(object);
    contentFilter.put(object);
  }

}
