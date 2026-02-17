package ru.danilarassokhin.statistic.util;

public class NullableAccumulator {

  public static Double sum(Double first, Double second) {
    if (first == null || first.isNaN()) {
      return second;
    }
    if (second == null || second.isNaN()) {
      return first;
    }
    return first + second;
  }

}
