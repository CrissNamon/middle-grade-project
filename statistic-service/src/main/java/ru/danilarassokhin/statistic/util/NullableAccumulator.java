package ru.danilarassokhin.statistic.util;

public class NullableAccumulator {

  public static Double sum(Double first, Double second) {
    if (first == null && second == null) {
      return 0.0;
    }
    if (first == null) {
      return second;
    }
    if (second == null) {
      return first;
    }
    return first + second;
  }

}
