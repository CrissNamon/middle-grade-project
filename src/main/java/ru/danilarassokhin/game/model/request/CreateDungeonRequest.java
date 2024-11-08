package ru.danilarassokhin.game.model.request;

import ru.danilarassokhin.game.entity.data.Dungeon;

public record CreateDungeonRequest(Integer level, Dungeon code) {

}
