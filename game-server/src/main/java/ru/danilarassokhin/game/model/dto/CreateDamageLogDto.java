package ru.danilarassokhin.game.model.dto;

/**
 * DTO for creation of new damage log entity.
 * @param playerId ID of player
 * @param dungeonId ID of dungeon
 */
public record CreateDamageLogDto(Integer playerId, Integer dungeonId) {}
