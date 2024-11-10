package ru.danilarassokhin.game.entity;

import ru.danilarassokhin.game.entity.data.Item;
import ru.danilarassokhin.game.sql.annotation.Column;
import ru.danilarassokhin.game.sql.annotation.Entity;

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

}
