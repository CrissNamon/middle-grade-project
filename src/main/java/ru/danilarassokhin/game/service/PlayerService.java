package ru.danilarassokhin.game.service;

import ru.danilarassokhin.game.model.dto.CreatePlayerDto;
import ru.danilarassokhin.game.model.dto.PlayerDto;

public interface PlayerService {

  PlayerDto createPlayer(CreatePlayerDto createPlayerDto);

}
