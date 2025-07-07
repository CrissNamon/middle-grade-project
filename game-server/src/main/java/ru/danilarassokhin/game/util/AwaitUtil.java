package ru.danilarassokhin.game.util;

import java.util.Arrays;
import java.util.function.Supplier;
import ru.danilarassokhin.injection.exception.ApplicationException;

import tech.hiddenproject.aide.optional.Action;

public class AwaitUtil {

  /**
   * Retries specified action if it throws errors.
   * @param times Max retries count
   * @param action Action to execute
   * @param onError Action to make on every error
   * @param errors Error classes to retry action for
   * @return Result of action
   * @param <T> Type of action result
   */
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

  /**
   * Retries specified action if it throws errors.
   * @param times Max retries count
   * @param action Action to execute
   * @param onError Action to make on every error
   * @param errors Error classes to retry action for
   */
  @SafeVarargs
  public static void retryOnError(Integer times, Action action, Action onError, Class<? extends Throwable>... errors) {
    AwaitUtil.retryOnError(times, () -> {
      action.make();
      return null;
    }, onError, errors);
  }

}
