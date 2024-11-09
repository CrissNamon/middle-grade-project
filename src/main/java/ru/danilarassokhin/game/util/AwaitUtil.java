package ru.danilarassokhin.game.util;

import java.util.Arrays;
import java.util.function.Supplier;

import ru.danilarassokhin.game.exception.ApplicationException;
import tech.hiddenproject.aide.optional.Action;

public class AwaitUtil {

  @SafeVarargs
  public static <T> T retryOnError(Integer times, Supplier<T> action, Action onError, Class<? extends Throwable>... errors) {
    var currentTimes = 1;
    while (currentTimes <= times) {
      try {
        return action.get();
      } catch (Throwable t) {
        if (Arrays.stream(errors).noneMatch(e -> t.getClass().equals(e)) || currentTimes == times) {
          throw t;
        }
        onError.make();
      } finally {
        currentTimes++;
      }
    }
    throw new ApplicationException("Wrong times specified");
  }
}
