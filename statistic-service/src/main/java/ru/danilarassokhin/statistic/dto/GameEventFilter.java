package ru.danilarassokhin.statistic.dto;

import java.time.LocalDateTime;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class GameEventFilter {

  private String type;
  private Integer playerId;
  private Integer bossId;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private LocalDateTime from;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private LocalDateTime to;

  private Integer limit = 100;
}
