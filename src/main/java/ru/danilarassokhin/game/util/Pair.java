package ru.danilarassokhin.game.util;

/**
 * Immutable pair of objects.
 *
 * @param first First object
 * @param second Second object
 * @param <A> Type of first object
 * @param <B> Type of second object
 */
public record Pair<A, B>(A first, B second) {

  /**
   * Creates new pair.
   */
  public static <F, S> Pair<F, S> of(F first, S second) {
    return new Pair<>(first, second);
  }

}
