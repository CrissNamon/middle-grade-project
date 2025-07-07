package ru.danilarassokhin.game.entity;

import ru.danilarassokhin.game.entity.data.Item;
import ru.danilarassokhin.sql.annotation.Column;
import ru.danilarassokhin.sql.annotation.Entity;

/**
 * Table for market items.
 * @param id Id of market item.
 * @param itemCode {@link Item}
 * @param price Price of item
 * @param amount Amount of items left
 */
@Entity(MarketEntity.TABLE_NAME)
public record MarketEntity(
    @Column("id") Integer id,
    @Column("item_code") Item itemCode,
    @Column("price") Integer price,
    @Column("amount") Integer amount
) {

  public static final String TABLE_NAME = "market";
  public static final String UX_MARKET_ITEM_PRICE_CONSTRAINT = "ux_market_item_price";

}
