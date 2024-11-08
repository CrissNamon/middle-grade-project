package ru.danilarassokhin.game.model.dto;

import ru.danilarassokhin.game.entity.data.Dungeon;

public record CreateDungeonDto(Integer level, Dungeon code) {

}
