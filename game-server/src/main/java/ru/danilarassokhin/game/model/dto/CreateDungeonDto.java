package ru.danilarassokhin.game.model.dto;

/**
 * DTO for creation of new dungeon entity.
 * @param level Dungeon level
 * @param code Dungeon code
 */
public record CreateDungeonDto(Integer level, String code) {}
