package ru.danilarassokhin.game.service.impl;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import ru.danilarassokhin.game.entity.PlayerEntity;
import ru.danilarassokhin.game.exception.ApplicationException;
import ru.danilarassokhin.game.mapper.PlayerMapper;
import ru.danilarassokhin.game.model.dto.CreatePlayerDto;
import ru.danilarassokhin.game.model.dto.PlayerDto;
import ru.danilarassokhin.game.repository.CamundaRepository;
import ru.danilarassokhin.game.repository.PlayerRepository;
import ru.danilarassokhin.game.service.PlayerService;
import ru.danilarassokhin.game.sql.service.TransactionManager;
import tech.hiddenproject.aide.optional.ThrowableOptional;
import tech.hiddenproject.progressive.annotation.Autofill;
import tech.hiddenproject.progressive.annotation.GameBean;

@GameBean
@RequiredArgsConstructor(onConstructor_ = {@Autofill})
public class PlayerServiceImpl implements PlayerService {

  private final TransactionManager transactionManager;
  private final PlayerRepository playerRepository;
  private final PlayerMapper playerMapper;
  private final CamundaRepository camundaService;

  @Override
  public PlayerDto create(CreatePlayerDto createPlayerDto) {
    Optional<PlayerEntity> createdPlayer = transactionManager.fetchInTransaction(ctx -> {
      if (!playerRepository.existsByName(ctx, createPlayerDto.name())) {
        var newPlayerId = playerRepository.save(ctx, playerMapper.createPlayerDtoToEntity(createPlayerDto));
        ThrowableOptional.sneaky(() -> camundaService.createProcess(newPlayerId).get(),
                                 e -> new ApplicationException("Exception occurred during player creation"));
        return playerRepository.findById(ctx, newPlayerId);
      }
      throw new ApplicationException("Player with name " + createPlayerDto.name() + " already exists");
    });
    return createdPlayer
        .map(playerMapper::playerEntityToDto)
        .orElseThrow(() -> new ApplicationException("Player creation failed"));
  }

  @Override
  public PlayerDto getById(Integer id) {
    return transactionManager.fetchInTransaction(ctx -> {
          ctx.readOnly();
          return playerRepository.findById(ctx, id);
        })
        .map(playerMapper::playerEntityToDto)
        .orElseThrow(() -> new ApplicationException("Player not found"));
  }
}
