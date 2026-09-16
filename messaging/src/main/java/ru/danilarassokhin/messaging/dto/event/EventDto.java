package ru.danilarassokhin.messaging.dto.event;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

/**
 * Основной класс для событий.
 */
@JsonTypeInfo(
    use = Id.NAME,
    include = As.PROPERTY,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = PlayerDealDamageEventDto.class, name = "PLAYER_DEAL_DAMAGE"),
    @JsonSubTypes.Type(value = BossSpawnedSystemEventDto.class, name = "SYSTEM_EVENT_BOSS_SPAWNED")
})
public class EventDto {

  private final UUID id;
  private final LocalDateTime dateTime;
  private final EventType type;

  public EventDto(UUID id, LocalDateTime dateTime, EventType type) {
    this.id = id;
    this.dateTime = dateTime;
    this.type = type;
  }

  public UUID getId() {
    return id;
  }

  public LocalDateTime getDateTime() {
    return dateTime;
  }

  public EventType getType() {
    return type;
  }
}
