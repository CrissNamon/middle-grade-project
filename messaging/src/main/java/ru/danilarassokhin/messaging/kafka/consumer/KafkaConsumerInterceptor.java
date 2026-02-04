package ru.danilarassokhin.messaging.kafka.consumer;

import java.lang.reflect.Method;

import org.springframework.messaging.Message;

public interface KafkaConsumerInterceptor<T> {

  /**
   * Определяет необходимость использования интерсептора.
   * @param message {@link Message}
   * @param bean Бин от которого вызывается метод
   * @param method Вызываемый метод
   * @return true - если нужно использовать интерсептор для метода
   */
  boolean filter(Message<?> message, Object bean, Method method);

  /**
   * Выполняется до резолва аргументов, которые будут переданы в метод.
   * @param message {@link Message}
   * @param bean Бин от которого вызывается метод
   * @param method Вызываемый метод
   * @param providedArgs Аргументы для резолва
   */
  default void beforeArgsEvaluation(Message<?> message, Object bean, Method method, Object... providedArgs) {}

  /**
   * Выполняется после резолва аргументов, которые будут переданы в метод.
   * @param message {@link Message}
   * @param bean Бин от которого вызывается метод
   * @param method Вызываемый метод
   * @param args Аргументы для вызова метода
   */
  default void beforeExecution(Message<?> message, Object bean, Method method, Object... args) {}

  /**
   * Выполняется после вызова метода.
   * @param result Резлуьтат выполнения
   * @param message {@link Message}
   * @param bean Бин от которого вызывается метод
   * @param method Вызываемый метод
   * @param args Аргументы вызова метода
   * @return Результат выполнения
   */
  default Object afterExecution(T result, Message<?> message, Object bean, Method method, Object... args) {
    return result;
  }

  /**
   * Выполняется в случае ошибки выполнения метода.
   * @param message {@link Message}
   * @param bean Бин от которого вызывается метод
   * @param method Вызываемый метод
   * @param args Аргументы вызова метода
   * @param throwable Ошибка
   */
  default void onExecutionError(Message<?> message, Object bean, Method method, Throwable throwable, Object... args) {
    throw new RuntimeException(throwable);
  }

}
