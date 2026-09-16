package ru.danilarassokhin.statistic.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import ru.danilarassokhin.statistic.config.IntegrationTest;
import ru.danilarassokhin.statistic.dto.GameEventDto;

public class GameEventServiceTest extends IntegrationTest {

  private static final String INSERT_SQL = """
    INSERT INTO game_events(id, dateTime, type, playerId, damage, bossId)
    VALUES (:id, :dateTime, :type, :playerId, :damage, :bossId);
  """;

  private static final String CLEAN_SQL = """
    TRUNCATE TABLE game_events;
  """;

  @Autowired
  private GameEventService gameEventService;

  @Autowired
  private JdbcClient jdbcClient;

  @BeforeEach
  void init() {
    jdbcClient.sql(CLEAN_SQL).update();
  }

  @Test
  public void itShouldReturnAllEvents() {
    var event = GameEventDto.builder()
      .id(UUID.randomUUID())
      .dateTime(LocalDateTime.now())
      .type("TEST_TYPE")
      .playerId(1)
      .damage(2.0)
      .bossId(3)
      .build();
    createGameEvent(event);
    var result = gameEventService.findByFilters(null, null, null, null, null, null);
    Assertions.assertEquals(1, result.size());
  }

  private void createGameEvent(GameEventDto dto) {
    jdbcClient.sql(INSERT_SQL)
      .param("id", dto.getId())
      .param("dateTime", dto.getDateTime())
      .param("type", dto.getType())
      .param("playerId", dto.getPlayerId())
      .param("damage", dto.getDamage())
      .param("bossId", dto.getBossId())
      .update();
  }


}
