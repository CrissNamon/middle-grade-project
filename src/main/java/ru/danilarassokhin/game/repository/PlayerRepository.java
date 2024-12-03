package ru.danilarassokhin.game.repository;

import java.util.List;

import ru.danilarassokhin.game.entity.PlayerEntity;
import ru.danilarassokhin.game.sql.repository.JdbcRepository;
import ru.danilarassokhin.game.sql.service.TransactionContext;

/**
 * Repository for {@link PlayerEntity}
 */
public interface PlayerRepository extends JdbcRepository<PlayerEntity, Integer> {

  /**
   * Checks if player with given name exists.
   * @param ctx {@link TransactionContext}
   * @param name Player's name
   * @return true if player with given name exists
   */
  boolean existsByName(TransactionContext ctx, String name);

  /**
   * Updates player.
   * @param ctx {@link TransactionContext}
   * @param playerEntity {@link PlayerEntity}
   */
  void update(TransactionContext ctx, PlayerEntity playerEntity);

  /**
   * Updates levels for players with given ids.
   * @param ctx {@link TransactionContext}
   * @param playerIds Ids of players for update
   */
  void updateLevelsForIds(TransactionContext ctx, List<Integer> playerIds);

  List<PlayerEntity> findAll();

}
