package ru.danilarassokhin.messaging.dto.event;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Системное событие о появлении босса.
 */
public class BossSpawnedSystemEventDto extends EventDto {

  private final Integer bossId;

  @JsonCreator
  public BossSpawnedSystemEventDto(
      @JsonProperty("id") UUID id,
      @JsonProperty("dateTime") LocalDateTime localDateTime,
      @JsonProperty("bossId") Integer bossId
  ) {
    super(id, localDateTime, EventType.SYSTEM_EVENT_BOSS_SPAWNED);
    this.bossId = bossId;
  }

  public Integer getBossId() {
    return bossId;
  }
}
