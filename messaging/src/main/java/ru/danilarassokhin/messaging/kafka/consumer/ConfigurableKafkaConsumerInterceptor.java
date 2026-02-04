package ru.danilarassokhin.messaging.kafka.consumer;

import java.lang.reflect.Method;

import org.springframework.messaging.Message;

/**
 * {@link KafkaConsumerInterceptor} с настраиваемым фильтром.
 */
public abstract class ConfigurableKafkaConsumerInterceptor<T> implements KafkaConsumerInterceptor<T> {

  private final KafkaConsumerInterceptorFilter filter;

  public ConfigurableKafkaConsumerInterceptor(KafkaConsumerInterceptorFilter filter) {
    this.filter = filter;
  }

  @Override
  public boolean filter(Message<?> message, Object bean, Method method) {
    return filter.test(message, bean, method);
  }
}
