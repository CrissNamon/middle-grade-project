package ru.danilarassokhin.statistic.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.danilarassokhin.statistic.dto.GameEventDto;
import ru.danilarassokhin.statistic.mapper.GameEventMapper;
import ru.danilarassokhin.statistic.service.GameEventService;

@Service
@RequiredArgsConstructor
public class GameEventServiceImpl implements GameEventService {

  private static final int DEFAULT_LIMIT = 100;

  private final GameEventMapper gameEventMapper;

  @Override
  public List<GameEventDto> findByFilters(String type, Integer playerId, Integer bossId,
                                          LocalDateTime from, LocalDateTime to, Integer limit) {
    return gameEventMapper.findByFilters(type, playerId, bossId, from, to, limit != null ? limit : DEFAULT_LIMIT);
  }
}
