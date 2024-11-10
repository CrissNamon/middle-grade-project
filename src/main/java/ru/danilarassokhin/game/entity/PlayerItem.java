package ru.danilarassokhin.game.entity;

import ru.danilarassokhin.game.entity.data.Item;
import ru.danilarassokhin.game.sql.annotation.Column;
import ru.danilarassokhin.game.sql.annotation.Entity;

/**
 * Table for player items.
 * @param id Item id
 * @param playerId Player id
 * @param item {@link Item}
 * @param amount Amount of items
 */
@Entity(PlayerItem.TABLE_NAME)
public record PlayerItem(
    @Column("id") Integer id,
    @Column("player_id") Integer playerId,
    @Column("item_code") Item item,
    @Column("amount") Integer amount
) {

  public static final String UX_PLAYER_ITEM_ID_CONSTRAINT = "ux_player_item_id";
  public static final String TABLE_NAME = "player_item";

}
