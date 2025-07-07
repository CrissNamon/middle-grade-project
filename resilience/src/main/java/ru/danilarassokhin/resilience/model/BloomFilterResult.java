package ru.danilarassokhin.resilience.model;

public enum BloomFilterResult {

  /**
   * Value has never been put into filter.
   */
  UNKNOWN,

  /**
   * Value might was put into filter, but filter definitely does not contain value.
   */
  NOT_CONTAINS,

  /**
   * Value might was put into filter and it might be present in filter.
   */
  MIGHT_CONTAINS

}
