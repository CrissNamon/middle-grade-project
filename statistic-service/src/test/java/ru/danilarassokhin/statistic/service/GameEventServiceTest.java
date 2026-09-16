package ru.danilarassokhin.statistic.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.danilarassokhin.statistic.config.IntegrationTest;
import ru.danilarassokhin.statistic.dto.GameEventDto;
import ru.danilarassokhin.statistic.mapper.GameEventMapper;

public class GameEventServiceTest extends IntegrationTest {

  @Autowired
  private GameEventMapper gameEventMapper;

  @Autowired
  private GameEventService gameEventService;

  @BeforeEach
  void init() {
    gameEventMapper.truncate();
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
    gameEventMapper.insert(event);
    var result = gameEventService.findByFilters(null, null, null, null, null, null);
    Assertions.assertEquals(1, result.size());
    Assertions.assertEquals(event.getId(), result.get(0).getId());
    Assertions.assertEquals(event.getType(), result.get(0).getType());
    Assertions.assertEquals(event.getDamage(), result.get(0).getDamage());
    Assertions.assertEquals(event.getPlayerId(), result.get(0).getPlayerId());
    Assertions.assertEquals(event.getBossId(), result.get(0).getBossId());
    Assertions.assertEquals(event.getDateTime().toLocalDate(), result.get(0).getDateTime().toLocalDate());
  }


}
