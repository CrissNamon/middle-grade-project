package ru.danilarassokhin.game.repository;

import ru.danilarassokhin.game.entity.MarketEntity;
import ru.danilarassokhin.game.sql.repository.JdbcRepository;
import ru.danilarassokhin.game.sql.service.TransactionContext;

/**
 * Repository for {@link MarketEntity}.
 */
public interface MarketRepository extends JdbcRepository<MarketEntity, Integer> {

  /**
   * Decreases item amount on market.
   * @param ctx {@link TransactionContext}
   * @param itemId ID of item
   */
  void decreaseItem(TransactionContext ctx, Integer itemId);

}
