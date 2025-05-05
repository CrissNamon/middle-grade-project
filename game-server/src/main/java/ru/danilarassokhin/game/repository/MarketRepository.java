package ru.danilarassokhin.game.repository;

import ru.danilarassokhin.game.entity.MarketEntity;
import ru.danilarassokhin.sql.repository.JdbcRepository;

/**
 * Repository for {@link MarketEntity}.
 */
public interface MarketRepository extends JdbcRepository<MarketEntity, Integer> {

  /**
   * Decreases item amount on market.
   * @param itemId ID of item
   */
  void decreaseItem(Integer itemId);

}
