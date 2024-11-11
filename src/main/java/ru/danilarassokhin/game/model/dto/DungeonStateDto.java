package ru.danilarassokhin.game.model.dto;

import ru.danilarassokhin.game.entity.data.Dungeon;
import ru.danilarassokhin.game.entity.data.DungeonState;

/**
 * DTO for current dungeon state.
 * @param state {@link DungeonState}
 * @param dungeon {@link Dungeon}
 */
public record DungeonStateDto(DungeonState state, Dungeon dungeon) {}
