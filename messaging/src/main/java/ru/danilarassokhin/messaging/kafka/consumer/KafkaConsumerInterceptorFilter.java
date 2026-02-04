package ru.danilarassokhin.messaging.kafka.consumer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.function.Predicate;

import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import ru.danilarassokhin.messaging.dto.KafkaHeader;

@FunctionalInterface
public interface KafkaConsumerInterceptorFilter {

  boolean test(Message<?> message, Object bean, Method method);

  default KafkaConsumerInterceptorFilter and(KafkaConsumerInterceptorFilter other) {
    return (message, bean, method) -> this.test(message, bean, method) && other.test(message, bean, method);
  }

  static KafkaConsumerInterceptorFilter filterByTopicName(String name) {
    return (message, bean, method) -> checkKafkaHeader(message, header -> header.equals(name));
  }

  static KafkaConsumerInterceptorFilter filterByTopicRegex(String regex) {
    return (message, bean, method) -> checkKafkaHeader(message, header -> header.matches(regex));
  }

  static KafkaConsumerInterceptorFilter filterByAnnotation(Class<? extends Annotation> annotationClass) {
    return (message, bean, method) -> method.isAnnotationPresent(annotationClass);
  }

  private static boolean checkKafkaHeader(Message<?> message, Predicate<String> predicate) {
    var kafkaHeader = message.getHeaders().get(KafkaHeaders.RECEIVED_KEY, String.class);
    var kafkaStreamsHeader = message.getHeaders().get(KafkaHeader.STREAMS_RECEIVED_TOPIC, String.class);
    if (kafkaHeader != null) {
      return predicate.test(kafkaHeader);
    }
    if (kafkaStreamsHeader != null) {
      return predicate.test(kafkaStreamsHeader);
    }
    return false;
  }

}
