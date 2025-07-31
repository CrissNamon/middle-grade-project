package ru.danilarassokhin.game.service.impl;

import lombok.RequiredArgsConstructor;
import ru.danilarassokhin.game.entity.MailEntity;
import ru.danilarassokhin.game.mapper.PlayerMapper;
import ru.danilarassokhin.game.model.dto.CreatePlayerDto;
import ru.danilarassokhin.game.model.dto.PlayerDto;
import ru.danilarassokhin.game.repository.MailRepository;
import ru.danilarassokhin.game.repository.PlayerRepository;
import ru.danilarassokhin.game.service.CamundaService;
import ru.danilarassokhin.game.service.PlayerService;
import ru.danilarassokhin.injection.exception.ApplicationException;
import ru.danilarassokhin.sql.annotation.Transactional;
import tech.hiddenproject.aide.optional.ThrowableOptional;
import tech.hiddenproject.progressive.annotation.Autofill;
import tech.hiddenproject.progressive.annotation.GameBean;

@GameBean
@RequiredArgsConstructor(onConstructor_ = {@Autofill})
public class PlayerServiceImpl implements PlayerService {

  private final PlayerRepository playerRepository;
  private final PlayerMapper playerMapper;
  private final CamundaService camundaService;
  private final MailRepository mailRepository;

  @Transactional
  @Override
  public PlayerDto create(CreatePlayerDto createPlayerDto) {
    if (!playerRepository.existsByName(createPlayerDto.name())) {
      var newPlayerId = playerRepository.save(playerMapper.createPlayerDtoToEntity(createPlayerDto));
      ThrowableOptional.sneaky(() -> camundaService.createProcess(newPlayerId),
                               e -> new ApplicationException("Exception occurred during player creation", e));
      var createdPlayer = playerRepository.findById(newPlayerId);
      mailRepository.save(new MailEntity(createPlayerDto.name(), "Successful registration"));
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
}
