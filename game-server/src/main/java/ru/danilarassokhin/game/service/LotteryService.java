package ru.danilarassokhin.game.service;

/**
 * Service for lottery.
 */
public interface LotteryService {

  /**
   * Tries to grab prize for player.
   * @param playerId Player id.
   */
  void attendLottery(Integer playerId);

}
