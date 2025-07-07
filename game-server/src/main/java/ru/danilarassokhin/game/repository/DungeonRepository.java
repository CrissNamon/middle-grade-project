package ru.danilarassokhin.game.repository;

import java.util.Optional;

import ru.danilarassokhin.game.entity.DungeonEntity;
import ru.danilarassokhin.sql.repository.JdbcRepository;

/**
 * Repository for {@link DungeonEntity}.
 */
public interface DungeonRepository extends JdbcRepository<DungeonEntity, Integer> {

  /**
   * Checks if dungeon exists by given level and code.
   * @param level Dungeon level
   * @param code Dungeon code
   * @return true if dungeon exists
   */
  boolean existsByLevelAndCode(Integer level, String code);

  /**
   * Searches for {@link DungeonEntity} by level.
   * @param level Level
   * @return Optional of {@link DungeonEntity}
   */
  Optional<DungeonEntity> findByLevel(Integer level);

}
