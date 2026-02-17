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
    @JsonSubTypes.Type(value = SystemEventDto.class, name = "SYSTEM_EVENT")
})
public class EventDto {

  private final UUID id;
  private final LocalDateTime localDateTime;
  private final EventType type;

  public EventDto(UUID id, LocalDateTime localDateTime, EventType type) {
    this.id = id;
    this.localDateTime = localDateTime;
    this.type = type;
  }

  public UUID getId() {
    return id;
  }

  public LocalDateTime getLocalDateTime() {
    return localDateTime;
  }

  public EventType getType() {
    return type;
  }
}
