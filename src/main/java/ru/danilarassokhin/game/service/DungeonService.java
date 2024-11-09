package ru.danilarassokhin.game.service;

import ru.danilarassokhin.game.model.dto.CreateDamageLogDto;
import ru.danilarassokhin.game.model.dto.DungeonDto;
import ru.danilarassokhin.game.model.dto.CreateDungeonDto;
import ru.danilarassokhin.game.model.dto.DungeonStateDto;

public interface DungeonService {

  DungeonDto save(CreateDungeonDto createDungeonDto);

  DungeonStateDto attack(CreateDamageLogDto createDamageLogDto);

}
