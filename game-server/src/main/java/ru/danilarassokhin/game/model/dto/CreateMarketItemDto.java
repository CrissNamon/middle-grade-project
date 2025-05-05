package ru.danilarassokhin.game.model.dto;

import ru.danilarassokhin.game.entity.data.Item;

/**
 * DTO for creation new market item.
 * @param item {@link Item}
 * @param price Price of item
 * @param amount Amount of item
 */
public record CreateMarketItemDto(
    Item item,
    Integer price,
    Integer amount
) {}
