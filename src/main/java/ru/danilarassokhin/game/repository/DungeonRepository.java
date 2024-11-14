package ru.danilarassokhin.game.repository;

import ru.danilarassokhin.game.entity.DungeonEntity;
import ru.danilarassokhin.game.entity.data.Dungeon;
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
   * @param code {@link Dungeon}
   * @return true if dungeon exists
   */
  boolean existsByLevelAndCode(TransactionContext ctx, Integer level, Dungeon code);

}
