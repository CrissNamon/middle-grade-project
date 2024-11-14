package ru.danilarassokhin.game.model.request;

import jakarta.validation.constraints.Min;

/**
 * DTO for attacking dungeon request.
 * @param playerId Player ID
 * @param dungeonId Dungeon ID
 */
public record DungeonAttackRequest(
    @Min(1) Integer playerId,
    @Min(1) Integer dungeonId
) {}
