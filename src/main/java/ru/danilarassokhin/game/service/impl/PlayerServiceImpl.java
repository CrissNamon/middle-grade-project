package ru.danilarassokhin.game.service.impl;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import ru.danilarassokhin.game.entity.PlayerEntity;
import ru.danilarassokhin.game.exception.ApplicationException;
import ru.danilarassokhin.game.mapper.PlayerMapper;
import ru.danilarassokhin.game.model.dto.CreatePlayerDto;
import ru.danilarassokhin.game.model.dto.PlayerDto;
import ru.danilarassokhin.game.repository.PlayerRepository;
import ru.danilarassokhin.game.service.PlayerService;
import ru.danilarassokhin.game.sql.service.TransactionManager;
import tech.hiddenproject.progressive.annotation.Autofill;
import tech.hiddenproject.progressive.annotation.GameBean;

@GameBean
@RequiredArgsConstructor(onConstructor_ = {@Autofill})
public class PlayerServiceImpl implements PlayerService {

  private final TransactionManager transactionManager;
  private final PlayerRepository playerRepository;
  private final PlayerMapper playerMapper;

  @Override
  public PlayerDto createPlayer(CreatePlayerDto createPlayerDto) {
    Optional<PlayerEntity> createdPlayer = transactionManager.fetchInTransaction(ctx -> {
      if (!playerRepository.existsByName(ctx, createPlayerDto.name())) {
        var newPlayerId = playerRepository.save(ctx, playerMapper.createPlayerDtoToEntity(createPlayerDto));
        return playerRepository.findById(ctx, newPlayerId);
      }
      throw new ApplicationException("Player with name " + createPlayerDto.name() + " already exists");
    });
    return createdPlayer
        .map(playerMapper::playerEntityToDto)
        .orElseThrow(() -> new ApplicationException("Player creation failed"));
  }
}
