package ru.danilarassokhin.messaging.dto.event;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Основной класс для событий инициированных игроком.
 */
public class PlayerEventDto extends EventDto {

  private final Integer playerId;

  public PlayerEventDto(UUID id, LocalDateTime localDateTime, EventType type, Integer playerId) {
    super(id, localDateTime, type);
    this.playerId = playerId;
  }

  public Integer getPlayerId() {
    return playerId;
  }
}
