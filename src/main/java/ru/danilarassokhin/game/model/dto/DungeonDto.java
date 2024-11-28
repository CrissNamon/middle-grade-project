package ru.danilarassokhin.game.model.dto;

/**
 * DTO for dungeon entity.
 * @param id ID of dungeon
 * @param level Level number
 * @param code Dungeon code
 */
public record DungeonDto(Integer id, Integer level, String code) {}
