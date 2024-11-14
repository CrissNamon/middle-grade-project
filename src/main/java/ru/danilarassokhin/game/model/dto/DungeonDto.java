package ru.danilarassokhin.game.model.dto;

import ru.danilarassokhin.game.entity.data.Dungeon;

/**
 * DTO for dungeon entity.
 * @param id ID of dungeon
 * @param level Level number
 * @param code {@link Dungeon}
 */
public record DungeonDto(Integer id, Integer level, Dungeon code) {}
