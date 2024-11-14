package ru.danilarassokhin.game.model.dto;

import ru.danilarassokhin.game.entity.data.Dungeon;

/**
 * DTO for creation of new dungeon entity.
 * @param level Dungeon level
 * @param code {@link Dungeon}
 */
public record CreateDungeonDto(Integer level, Dungeon code) {}
