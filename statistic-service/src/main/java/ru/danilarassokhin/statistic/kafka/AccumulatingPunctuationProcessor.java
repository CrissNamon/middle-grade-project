package ru.danilarassokhin.statistic.kafka;

import java.time.Duration;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;

/**
 * Группирует и аккумулирует сообщения по заданной функции.
 * @param <EVENT> Тип оригинального сообщения
 * @param <KEY> Тип нового ключа
 * @param <VALUE> Тип нового сообщения
 */
public class AccumulatingPunctuationProcessor<EVENT, KEY, VALUE> implements Processor<String, EVENT, KEY, VALUE> {

  private final String storeName;
  private final Function<Record<String, EVENT>, KEY> keyMapping;
  private final Function<Record<String, EVENT>, VALUE> valueMapping;
  private final BiFunction<VALUE, VALUE, VALUE> accumulator;
  private final Duration interval;

  private KeyValueStore<KEY, VALUE> stateStore;

  /**
   * @param storeName Название хранилища Kafka Streams.
   * @param keyMapping Функция для мамминга ключа
   * @param valueMapping Функция для маппинга значения
   * @param accumulator Функция аккумулятор
   * @param interval Интервал группировки
   */
  public AccumulatingPunctuationProcessor(
      String storeName,
      Function<Record<String, EVENT>, KEY> keyMapping,
      Function<Record<String, EVENT>, VALUE> valueMapping,
      BiFunction<VALUE, VALUE, VALUE> accumulator,
      Duration interval
  ) {
    this.storeName = storeName;
    this.keyMapping = keyMapping;
    this.valueMapping = valueMapping;
    this.accumulator = accumulator;
    this.interval = interval;
  }

  @Override
  public void init(ProcessorContext<KEY, VALUE> context) {
    this.stateStore = context.getStateStore(storeName);
    context.schedule(interval, PunctuationType.WALL_CLOCK_TIME, timestamp -> {
      try (KeyValueIterator<KEY, VALUE> iterator = stateStore.all()) {
        while (iterator.hasNext()) {
          KeyValue<KEY, VALUE> entry = iterator.next();
          context.forward(new Record<>(entry.key, entry.value, timestamp));
          stateStore.delete(entry.key);
        }
      }
    });
  }

  @Override
  public void process(Record<String, EVENT> record) {
    var mappedKey = keyMapping.apply(record);
    var currentValue = stateStore.get(mappedKey);
    var newValue = valueMapping.apply(record);
    var result = accumulator.apply(currentValue, newValue);
    stateStore.put(mappedKey, result);
  }
}
