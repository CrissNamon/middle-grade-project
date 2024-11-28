package ru.danilarassokhin.game.repository;

import java.util.Optional;

import ru.danilarassokhin.game.entity.DungeonEntity;
import ru.danilarassokhin.game.sql.repository.JdbcRepository;
import ru.danilarassokhin.game.sql.service.TransactionContext;

/**
 * Repository for {@link DungeonEntity}.
 */
public interface DungeonRepository extends JdbcRepository<DungeonEntity, Integer> {

  /**
   * Checks if dungeon exists by given level and code.
   * @param ctx {@link TransactionContext}
   * @param level Dungeon level
   * @param code Dungeon code
   * @return true if dungeon exists
   */
  boolean existsByLevelAndCode(TransactionContext ctx, Integer level, String code);

  /**
   * Searches for {@link DungeonEntity} by level.
   * @param ctx {@link TransactionContext}
   * @param level Level
   * @return Optional of {@link DungeonEntity}
   */
  Optional<DungeonEntity> findByLevel(TransactionContext ctx, Integer level);

}
