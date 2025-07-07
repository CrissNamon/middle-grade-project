package ru.danilarassokhin.game.model.dto;

import ru.danilarassokhin.game.entity.MarketEntity;
import ru.danilarassokhin.game.entity.data.Item;

/**
 * DTO for {@link MarketEntity}.
 * @param id Item id on market
 * @param item {@link Item}
 * @param price Price of item
 * @param amount Amount of item
 */
public record MarketItemDto(
    Integer id,
    Item item,
    Integer price,
    Integer amount
) {

}
