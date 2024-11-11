package ru.danilarassokhin.game.model.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import ru.danilarassokhin.game.entity.data.Item;

/**
 * Request for creation of new market item.
 * @param item {@link Item}
 * @param price Price of item
 * @param amount Amount of item
 */
public record CreateMarketItemRequest(
    @NotNull Item item,
    @Min(1) Integer price,
    @Min(1) Integer amount
) {}
