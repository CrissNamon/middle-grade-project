package ru.danilarassokhin.messaging.kafka;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

/**
 * {@link KafkaProducer} с возможностью добавления интерсепторов.
 * @param <K> Тип ключа
 * @param <V> Тип значения
 */
public class DelegatingKafkaProducer<K, V> extends KafkaProducer<K, V> implements Producer<K, V> {

  private final List<KafkaProducerInterceptor<K, V>> interceptors;

  public DelegatingKafkaProducer(Properties properties, List<KafkaProducerInterceptor<K, V>> interceptors) {
    super(properties);
    this.interceptors = interceptors;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Future<RecordMetadata> send(ProducerRecord<K, V> record) {
    return interceptors.stream().filter(interceptor -> interceptor.filter(record))
        .findFirst()
        .map(interceptor -> super.send(interceptor.beforeSend(record)))
        .orElseGet(() -> super.send(record));
  }

}
