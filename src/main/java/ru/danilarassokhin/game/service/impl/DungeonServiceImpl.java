package ru.danilarassokhin.game.service.impl;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import ru.danilarassokhin.game.exception.ApplicationException;
import ru.danilarassokhin.game.mapper.DungeonMapper;
import ru.danilarassokhin.game.model.dto.DungeonDto;
import ru.danilarassokhin.game.model.dto.CreateDungeonDto;
import ru.danilarassokhin.game.repository.DungeonRepository;
import ru.danilarassokhin.game.service.DungeonService;
import ru.danilarassokhin.game.sql.service.TransactionManager;
import tech.hiddenproject.progressive.annotation.Autofill;
import tech.hiddenproject.progressive.annotation.GameBean;

@GameBean
@RequiredArgsConstructor(onConstructor_ = {@Autofill})
public class DungeonServiceImpl implements DungeonService {

  private final TransactionManager transactionManager;
  private final DungeonMapper dungeonMapper;
  private final DungeonRepository dungeonRepository;

  @Override
  public DungeonDto save(CreateDungeonDto createDungeonDto) {
    return transactionManager.fetchInTransaction(ctx -> {
      if (!dungeonRepository.existsByLevelAndCode(ctx, createDungeonDto.level(), createDungeonDto.code())) {
        var id = dungeonRepository.save(ctx, dungeonMapper.createDungeonDtoToEntity(createDungeonDto));
        return dungeonRepository.findById(ctx, id);
      }
      throw new ApplicationException("Dungeon already exists");
    }).map(dungeonMapper::dungeonEntityToDto).orElseThrow(() -> new ApplicationException("Dungeon save failed"));
  }
}
