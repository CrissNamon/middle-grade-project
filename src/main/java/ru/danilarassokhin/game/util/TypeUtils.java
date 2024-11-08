package ru.danilarassokhin.game.util;

/**
 * Utils for types.
 */
public class TypeUtils {

  public static boolean isInt(Class<?> type) {
    return type.equals(Integer.class) || type.equals(int.class);
  }

  public static boolean isLong(Class<?> type) {
    return type.equals(Long.class) || type.equals(long.class);
  }

  public static boolean isDouble(Class<?> type) {
    return type.equals(Double.class) || type.equals(double.class);
  }

  public static boolean isBoolean(Class<?> type) {
    return type.equals(Boolean.class) || type.equals(boolean.class);
  }

  public static boolean isString(Class<?> type) {
    return type.equals(String.class);
  }

  public static boolean isPrimitiveOrWrapper(Class<?> type) {
    return isInt(type) || isLong(type) || isBoolean(type) || isDouble(type) || isString(type);
  }

  @SuppressWarnings("unchecked")
  public static <T> T cast(Object object) {
    return (T) object;
  }
}
