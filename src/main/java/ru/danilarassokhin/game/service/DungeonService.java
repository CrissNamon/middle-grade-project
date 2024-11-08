package ru.danilarassokhin.game.service;

import ru.danilarassokhin.game.model.dto.DungeonDto;
import ru.danilarassokhin.game.model.dto.CreateDungeonDto;

public interface DungeonService {

  DungeonDto save(CreateDungeonDto createDungeonDto);

}
