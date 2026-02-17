package ru.danilarassokhin.statistic.util;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.tuple.ImmutablePair;
import ru.danilarassokhin.messaging.dto.event.BossSpawnedSystemEventDto;
import ru.danilarassokhin.messaging.dto.event.EventDto;
import ru.danilarassokhin.messaging.dto.event.PlayerDealDamageEventDto;

public class PojoCreator {

  public static PlayerDealDamageEventDto createPlayerDealDamageEventDto() {
    return new PlayerDealDamageEventDto(UUID.randomUUID(), 1, LocalDateTime.now(), 10.0);
  }

  public static BossSpawnedSystemEventDto createBossSpawnedSystemEventDto() {
    return new BossSpawnedSystemEventDto(UUID.randomUUID(), LocalDateTime.now(), 1);
  }

  public static List<ImmutablePair<EventDto, String>> createEventsAndFeedMessages() {
    return List.of(
        ImmutablePair.of(
            createPlayerDealDamageEventDto(), "Игрок.*"
        ),
        ImmutablePair.of(
            createBossSpawnedSystemEventDto(), "В игровом мире.*"
        )
    );
  }

}
