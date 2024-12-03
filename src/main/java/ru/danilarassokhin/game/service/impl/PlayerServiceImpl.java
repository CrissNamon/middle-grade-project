package ru.danilarassokhin.game.service.impl;

import java.util.List;

import lombok.RequiredArgsConstructor;
import ru.danilarassokhin.game.entity.PlayerEntity;
import ru.danilarassokhin.game.exception.ApplicationException;
import ru.danilarassokhin.game.mapper.PlayerMapper;
import ru.danilarassokhin.game.model.dto.CreatePlayerDto;
import ru.danilarassokhin.game.model.dto.PlayerDto;
import ru.danilarassokhin.game.repository.PlayerRepository;
import ru.danilarassokhin.game.service.CamundaService;
import ru.danilarassokhin.game.service.PlayerService;
import ru.danilarassokhin.game.sql.annotation.Transactional;
import tech.hiddenproject.aide.optional.ThrowableOptional;
import tech.hiddenproject.progressive.annotation.Autofill;
import tech.hiddenproject.progressive.annotation.GameBean;

@GameBean
@RequiredArgsConstructor(onConstructor_ = {@Autofill})
public class PlayerServiceImpl implements PlayerService {

  private final PlayerRepository playerRepository;
  private final PlayerMapper playerMapper;
  private final CamundaService camundaService;

  @Transactional
  @Override
  public PlayerDto create(CreatePlayerDto createPlayerDto) {
    if (!playerRepository.existsByName(createPlayerDto.name())) {
      var newPlayerId = playerRepository.save(playerMapper.createPlayerDtoToEntity(createPlayerDto));
      ThrowableOptional.sneaky(() -> camundaService.createProcess(newPlayerId),
                               e -> new ApplicationException("Exception occurred during player creation"));
      var createdPlayer = playerRepository.findById(newPlayerId);
      return createdPlayer
          .map(playerMapper::playerEntityToDto)
          .orElseThrow(() -> new ApplicationException("Player creation failed"));
    }
    throw new ApplicationException("Player with name " + createPlayerDto.name() + " already exists");
  }

  @Override
  public PlayerDto getById(Integer id) {
    return playerRepository.findById(id)
        .map(playerMapper::playerEntityToDto)
        .orElseThrow(() -> new ApplicationException("Player not found"));
  }

  @Override
  @Transactional
  public List<PlayerEntity> findAll() {
    return playerRepository.findAll();
  }
}
