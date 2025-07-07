package ru.danilarassokhin.game.model.request;

import jakarta.validation.constraints.Min;

/**
 * DTO for creating new dungeon request.
 * @param level Dungeon level
 * @param code Dungeon code
 */
public record CreateDungeonRequest(
    @Min(1) Integer level,
    String code
) {}
