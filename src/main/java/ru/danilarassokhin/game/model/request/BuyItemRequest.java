package ru.danilarassokhin.game.model.request;

import jakarta.validation.constraints.Min;

/**
 * DTO for buying item request.
 * @param playerId Player ID
 * @param itemId Item ID
 */
public record BuyItemRequest(
    @Min(1) Integer playerId,
    @Min(1)Integer itemId
) {}
