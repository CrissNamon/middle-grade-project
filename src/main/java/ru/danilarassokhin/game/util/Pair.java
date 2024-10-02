package ru.danilarassokhin.game.util;

public record Pair<A, B>(A first, B second) {

  public static <F, S> Pair<F, S> of(F first, S second) {
    return new Pair<>(first, second);
  }

}
