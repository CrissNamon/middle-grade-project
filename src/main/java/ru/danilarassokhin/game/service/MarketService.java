package ru.danilarassokhin.game.service;

import ru.danilarassokhin.game.model.dto.CreateMarketItemDto;
import ru.danilarassokhin.game.model.dto.MarketItemDto;

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
  void buy(Integer playerId, Integer itemId);

  /**
   * Creates new item on market or updates existing item price and amount.
   * @param createMarketItemDto {@link CreateMarketItemDto}
   * @return {@link MarketItemDto}
   */
  MarketItemDto create(CreateMarketItemDto createMarketItemDto);

}
