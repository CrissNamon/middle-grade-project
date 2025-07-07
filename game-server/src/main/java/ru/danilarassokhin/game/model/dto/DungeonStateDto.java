package ru.danilarassokhin.game.model.dto;

import ru.danilarassokhin.game.entity.data.DungeonState;

/**
 * DTO for current dungeon state.
 * @param state {@link DungeonState}
 * @param dungeon Dungeon code
 */
public record DungeonStateDto(DungeonState state, String dungeon) {}
