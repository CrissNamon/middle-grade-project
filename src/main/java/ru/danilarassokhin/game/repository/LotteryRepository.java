package ru.danilarassokhin.game.repository;

import ru.danilarassokhin.game.entity.PlayerEntity;
import ru.danilarassokhin.game.sql.service.TransactionContext;

/**
 * Repository to work with money.
 */
public interface LotteryRepository {

  /**
   * Adds player to lottery table.
   * @param ctx {@link TransactionContext}
   * @param playerEntity {@link PlayerEntity}
   */
  void addPlayer(TransactionContext ctx, PlayerEntity playerEntity);

  /**
   * Checks if player exists in lottery table.
   * @param ctx {@link TransactionContext}
   * @param playerEntity {@link PlayerEntity}
   * @return true if player exists in lottery table
   */
  boolean existsByPlayer(TransactionContext ctx, PlayerEntity playerEntity);

  /**
   * Counts players who grabbed prizes.
   * @param ctx {@link TransactionContext}
   * @return Players count
   */
  Long countPlayers(TransactionContext ctx);

}
