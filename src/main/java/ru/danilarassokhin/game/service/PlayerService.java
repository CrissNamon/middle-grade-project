package ru.danilarassokhin.game.service;

import ru.danilarassokhin.game.model.CreatePlayerDto;

public interface PlayerService {

  void save(CreatePlayerDto createPlayerDto);

}
