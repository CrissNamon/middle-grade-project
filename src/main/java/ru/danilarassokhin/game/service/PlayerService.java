package ru.danilarassokhin.game.service;

import ru.danilarassokhin.game.model.dto.CreatePlayerDto;
import ru.danilarassokhin.game.model.dto.PlayerDto;

/**
 * Service for player operations.
 */
public interface PlayerService {

  /**
   * Creates new player.
   * @param createPlayerDto {@link CreatePlayerDto}
   * @return {@link PlayerDto}
   */
  PlayerDto create(CreatePlayerDto createPlayerDto);

  /**
   * Searches player by id.
   * @param id Player id
   * @return {@link PlayerDto}
   */
  PlayerDto getById(Integer id);

}
