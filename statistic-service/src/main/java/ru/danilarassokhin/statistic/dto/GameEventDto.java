package ru.danilarassokhin.statistic.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Data;

/**
 * DTO для событий из таблицы game_events в ClickHouse.
 */
@Data
public class GameEventDto {

  private UUID id;
  private LocalDateTime dateTime;
  private String type;
  private Integer playerId;
  private Double damage;
  private Integer bossId;
}
