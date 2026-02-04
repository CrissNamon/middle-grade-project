package ru.danilarassokhin.messaging.kafka.producer;

import java.util.Arrays;
import java.util.function.Predicate;

import org.apache.kafka.clients.producer.ProducerRecord;

/**
 * Интерсептор для {@link DelegatingKafkaProducer}.
 * @param <K> Тип ключа
 * @param <V> Тип значения
 */
public interface KafkaProducerInterceptor<K, V> {

  /**
   * Определяет нужно ли применять интерсептор для {@link ProducerRecord}.
   * @param record {@link ProducerRecord}
   * @return true - если нужно применить интерсептор
   */
  boolean filter(ProducerRecord<K, V> record);

  /**
   * Выполняется до вызова {@link org.apache.kafka.clients.producer.Producer#send(ProducerRecord)}.
   * @param record {@link ProducerRecord}
   * @return Измененный {@link ProducerRecord}
   */
  default ProducerRecord<K, V> beforeSend(ProducerRecord<K, V> record) {
    return record;
  }

  static <K, V> Predicate<ProducerRecord<K, V>> filterTopicByRegex(String regex) {
    return (record) -> record.topic().matches(regex);
  }

  static <K, V> Predicate<ProducerRecord<K, V>> filterTopicByName(String... names) {
    return (record) -> Arrays.stream(names).anyMatch(topicName -> topicName.equals(record.topic()));
  }

  static <K, V> Predicate<ProducerRecord<K, V>> filterByHeaderPresence(String headerName) {
    return (record) -> record.headers().headers(headerName).iterator().hasNext();
  }
}
