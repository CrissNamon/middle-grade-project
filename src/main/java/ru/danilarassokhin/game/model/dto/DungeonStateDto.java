package ru.danilarassokhin.game.model.dto;

import ru.danilarassokhin.game.entity.data.Dungeon;
import ru.danilarassokhin.game.entity.data.DungeonState;

public record DungeonStateDto(DungeonState state, Dungeon dungeon) {

}
