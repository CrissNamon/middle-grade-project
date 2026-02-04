package ru.danilarassokhin.messaging.kafka.producer;

import java.util.function.Predicate;

import org.apache.kafka.clients.producer.ProducerRecord;

/**
 * {@link KafkaProducerInterceptor} с настраиваемым фильтром.
 */
public abstract class ConfigurableKafkaProducerInterceptor<K, V> implements KafkaProducerInterceptor<K, V> {

  private final Predicate<ProducerRecord<K, V>> filter;

  public ConfigurableKafkaProducerInterceptor(Predicate<ProducerRecord<K, V>> filter) {
    this.filter = filter;
  }

  @Override
  public boolean filter(ProducerRecord<K, V> record) {
    return filter.test(record);
  }
}
