package ru.danilarassokhin.game.repository;

import ru.danilarassokhin.game.entity.PlayerEntity;

/**
 * Repository to work with money.
 */
public interface LotteryRepository {

  /**
   * Adds player to lottery table.
   * @param playerEntity {@link PlayerEntity}
   */
  void addPlayer(PlayerEntity playerEntity);

  /**
   * Checks if player exists in lottery table.
   * @param playerEntity {@link PlayerEntity}
   * @return true if player exists in lottery table
   */
  boolean existsByPlayer(PlayerEntity playerEntity);

  /**
   * Counts players who grabbed prizes.
   * @return Players count
   */
  Long countPlayers();

}
