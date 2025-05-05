package ru.danilarassokhin.game.service;

import ru.danilarassokhin.game.model.dto.CreateDamageLogDto;
import ru.danilarassokhin.game.model.dto.CreateDungeonDto;
import ru.danilarassokhin.game.model.dto.DungeonDto;
import ru.danilarassokhin.game.model.dto.DungeonStateDto;

/**
 * Service for operations with dungeon.
 */
public interface DungeonService {

  /**
   * Creates new dungeon.
   * @param createDungeonDto {@link CreateDungeonDto}
   * @return Created {@link DungeonDto}
   */
  DungeonDto save(CreateDungeonDto createDungeonDto);

  /**
   * Attacks dungeon with player.
   * @param createDamageLogDto {@link CreateDamageLogDto}
   * @return {@link DungeonStateDto}
   */
  DungeonStateDto attack(CreateDamageLogDto createDamageLogDto);

  /**
   * Searches for Dungeon by level.
   * @param level Level
   * @return {@link DungeonDto}
   */
  DungeonDto findByLevel(Integer level);

}
