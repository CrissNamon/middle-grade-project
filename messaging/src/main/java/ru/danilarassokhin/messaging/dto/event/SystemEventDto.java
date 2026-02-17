package ru.danilarassokhin.messaging.dto.event;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Системное событие.
 */
public class SystemEventDto extends EventDto {

  @JsonCreator
  public SystemEventDto(
      @JsonProperty("id") UUID id,
      @JsonProperty("dateTime") LocalDateTime localDateTime
  ) {
    super(id, localDateTime, EventType.SYSTEM_EVENT);
  }
}
