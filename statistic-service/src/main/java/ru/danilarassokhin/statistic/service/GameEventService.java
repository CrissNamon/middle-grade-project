package ru.danilarassokhin.statistic.service;

import java.time.LocalDateTime;
import java.util.List;

import ru.danilarassokhin.statistic.dto.GameEventDto;

public interface GameEventService {

  List<GameEventDto> findByFilters(
    String type,
    Integer playerId,
    Integer bossId,
    LocalDateTime from,
    LocalDateTime to,
    Integer limit
  );

}
