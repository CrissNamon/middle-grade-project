package ru.danilarassokhin.statistic.mapper;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import ru.danilarassokhin.statistic.dto.GameEventDto;

/**
 * MyBatis маппер для чтения событий из таблицы game_events в ClickHouse.
 */
@Mapper
public interface GameEventMapper {

  List<GameEventDto> findByFilters(
      @Param("type") String type,
      @Param("playerId") Integer playerId,
      @Param("bossId") Integer bossId,
      @Param("from") LocalDateTime from,
      @Param("to") LocalDateTime to,
      @Param("limit") Integer limit
  );
}
