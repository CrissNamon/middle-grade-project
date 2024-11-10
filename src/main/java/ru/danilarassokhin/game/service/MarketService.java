package ru.danilarassokhin.game.service;

/**
 * Service for market operations.
 */
public interface MarketService {

  /**
   * But item for player.
   *
   * @param playerId ID of player
   * @param itemId ID of player
   */
  void buyItem(Integer playerId, Integer itemId);

}
