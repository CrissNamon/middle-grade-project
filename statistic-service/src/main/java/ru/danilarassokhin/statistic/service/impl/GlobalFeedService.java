package ru.danilarassokhin.statistic.service.impl;

import jakarta.annotation.PreDestroy;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import ru.danilarassokhin.messaging.dto.event.BossSpawnedSystemEventDto;
import ru.danilarassokhin.statistic.dto.FeedDto;
import ru.danilarassokhin.statistic.service.FeedService;

/**
 * Работает с глобальной лентой активности.
 */
@Service
public class GlobalFeedService implements FeedService<FeedDto> {

  private final Sinks.Many<FeedDto> sinks = Sinks.many().multicast().onBackpressureBuffer();

  @Autowired
  public GlobalFeedService(
      KStream<String, Double> playerDamageStream,
      KStream<String, BossSpawnedSystemEventDto> bossSpawnedEventStream
  ) {
    var playerDamageFeed = playerDamageStream.map(this::createFeedDto);
    var systemEventsFeed = bossSpawnedEventStream.map(this::createFeedDto);
    systemEventsFeed.merge(playerDamageFeed).foreach((key, value) -> sinks.tryEmitNext(value));
  }

  /**
   * @return Лента активности всех пользователей
   */
  @Override
  public Flux<FeedDto> getAll() {
    return sinks.asFlux();
  }

  @PreDestroy
  public void close() {
    sinks.tryEmitComplete();
  }

  private KeyValue<String, FeedDto> createFeedDto(String playerName, Double damage) {
     return new KeyValue<>(playerName, new FeedDto("Игрок " + playerName + " нанес " + damage + " урона"));
  }

  private KeyValue<String, FeedDto> createFeedDto(String id, BossSpawnedSystemEventDto eventDto) {
    return new KeyValue<>(id, new FeedDto("В игровом мире появился босс " + eventDto.getBossId()));
  }

}
