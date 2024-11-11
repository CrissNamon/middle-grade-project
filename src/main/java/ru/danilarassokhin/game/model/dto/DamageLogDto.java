package ru.danilarassokhin.game.model.dto;

/**
 * DTO for damage log entity.
 * @param dungeonId ID of dungeon
 * @param playerId ID of player
 * @param damage Amount of damage
 * @param active true for active dungeon
 */
public record DamageLogDto(Integer dungeonId, Integer playerId, Integer damage, Boolean active) {}
