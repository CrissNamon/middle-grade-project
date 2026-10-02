package ru.danilarassokhin.statistic.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import ru.danilarassokhin.statistic.dto.GameEventDto;
import ru.danilarassokhin.statistic.dto.GameEventFilter;

/**
 * MyBatis маппер для чтения событий из таблицы game_events в ClickHouse.
 */
@Mapper
public interface GameEventMapper {

  List<GameEventDto> findByFilters(GameEventFilter filter);

  void insert(GameEventDto gameEventDto);

  void truncate();
}
