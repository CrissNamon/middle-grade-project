package ru.danilarassokhin.game.service.impl;

import lombok.RequiredArgsConstructor;
import ru.danilarassokhin.game.exception.ApplicationException;
import ru.danilarassokhin.game.mapper.PlayerMapper;
import ru.danilarassokhin.game.model.CreatePlayerDto;
import ru.danilarassokhin.game.repository.PlayerRepository;
import ru.danilarassokhin.game.service.PlayerService;
import tech.hiddenproject.progressive.annotation.Autofill;
import tech.hiddenproject.progressive.annotation.GameBean;

@GameBean
@RequiredArgsConstructor(onConstructor_ = {@Autofill})
public class PlayerServiceImpl implements PlayerService {

  private final PlayerRepository playerRepository;
  private final PlayerMapper playerMapper;

  @Override
  public void save(CreatePlayerDto createPlayerDto) {
    if (!playerRepository.existsByName(createPlayerDto.name())) {
      playerRepository.save(playerMapper.createPlayerDtoToPlayerEntity(createPlayerDto));
      return;
    }
    throw new ApplicationException("Player already exists!");
  }
}
