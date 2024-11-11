package ru.danilarassokhin.game.repository;

import java.util.List;

import ru.danilarassokhin.game.entity.DamageLogEntity;
import ru.danilarassokhin.game.sql.repository.JdbcRepository;
import ru.danilarassokhin.game.sql.service.TransactionContext;

/**
 * Repository for {@link DamageLogEntity}.
 */
public interface DamageLogRepository extends JdbcRepository<DamageLogEntity, Integer> {

  /**
   * Counts current damage for given dungeon id.
   * @param ctx {@link TransactionContext}
   * @param dungeonId ID of dungeon
   * @return Sum of damage
   */
  Long countDamage(TransactionContext ctx, Integer dungeonId);

  /**
   * Revives dungeon with given id.
   * @param ctx {@link TransactionContext}
   * @param dungeonId ID of dungeon
   */
  void revive(TransactionContext ctx, Integer dungeonId);

  /**
   * Searches for ids of active players for given dungeon id.
   * @param ctx {@link TransactionContext}
   * @param dungeonId ID of dungeon
   * @return List of player ids
   */
  List<Integer> findPlayersForActiveDungeon(TransactionContext ctx, Integer dungeonId);

}
