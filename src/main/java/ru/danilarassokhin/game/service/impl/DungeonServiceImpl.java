package ru.danilarassokhin.game.service.impl;

import java.sql.Connection;
import java.sql.SQLException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.danilarassokhin.game.entity.DamageLogEntity;
import ru.danilarassokhin.game.entity.DungeonEntity;
import ru.danilarassokhin.game.entity.PlayerEntity;
import ru.danilarassokhin.game.entity.data.DungeonState;
import ru.danilarassokhin.game.exception.ApplicationException;
import ru.danilarassokhin.game.exception.DataSourceException;
import ru.danilarassokhin.game.mapper.DungeonMapper;
import ru.danilarassokhin.game.model.dto.CreateDamageLogDto;
import ru.danilarassokhin.game.model.dto.DungeonDto;
import ru.danilarassokhin.game.model.dto.CreateDungeonDto;
import ru.danilarassokhin.game.model.dto.DungeonStateDto;
import ru.danilarassokhin.game.repository.DamageLogRepository;
import ru.danilarassokhin.game.repository.DungeonRepository;
import ru.danilarassokhin.game.repository.PlayerRepository;
import ru.danilarassokhin.game.service.DungeonService;
import ru.danilarassokhin.game.sql.service.TransactionContext;
import ru.danilarassokhin.game.sql.service.TransactionManager;
import ru.danilarassokhin.game.util.AwaitUtil;
import tech.hiddenproject.progressive.annotation.Autofill;
import tech.hiddenproject.progressive.annotation.GameBean;

@GameBean
@RequiredArgsConstructor(onConstructor_ = {@Autofill})
@Slf4j
public class DungeonServiceImpl implements DungeonService {

  private final TransactionManager transactionManager;
  private final DungeonMapper dungeonMapper;
  private final DungeonRepository dungeonRepository;
  private final DamageLogRepository damageLogRepository;
  private final PlayerRepository playerRepository;

  @Override
  public DungeonDto save(CreateDungeonDto createDungeonDto) {
    return transactionManager.fetchInTransaction(ctx -> {
      if (!dungeonRepository.existsByLevelAndCode(ctx, createDungeonDto.level(), createDungeonDto.code())) {
        var id = dungeonRepository.save(ctx, dungeonMapper.createDungeonDtoToEntity(createDungeonDto));
        return dungeonRepository.findById(ctx, id);
      }
      throw new ApplicationException("Dungeon already exists");
    }).map(dungeonMapper::dungeonEntityToDto)
        .orElseThrow(() -> new ApplicationException("Dungeon save failed"));
  }

  //Используем SERIALIZABLE, т.к. транзакция выполняет аггрегацию и вставку новых строк в таблицу
  //Несколько игроков атакуют одного монстра => строки с уроном должны вставляться строго последовательно
  //Т.е. нужна полная изоляция
  //REPEATABLE READ обеспечивает изоляцию только для изменения строк несколькими транзакциями
  //Т.к. в Postgres SERIALIZABLE использует предикатные блокировки, транзакция может упасть с ошибкой сериализации
  //Повторяем транзакцию пока не выполнится успешно, но не более 10 раз
  @Override
  public DungeonStateDto attack(CreateDamageLogDto createDamageLogDto) {
    return AwaitUtil.retryOnError(10, () -> attackDungeonTransaction(createDamageLogDto),
      () -> log.warn("Transaction attack(CreateDamageLogDto) failed. Retrying..."),
      DataSourceException.class, SQLException.class);
  }

  private DungeonStateDto attackDungeonTransaction(CreateDamageLogDto createDamageLogDto) {
    try {
      return transactionManager.fetchInTransaction(Connection.TRANSACTION_SERIALIZABLE, ctx -> {
        var dungeon = dungeonRepository.findById(ctx, createDamageLogDto.dungeonId())
            .orElseThrow(() -> new ApplicationException("Dungeon not found"));
        var player = playerRepository.findById(ctx, createDamageLogDto.playerId())
            .orElseThrow(() -> new ApplicationException("Player not found"));
        var newDamage = calculateDamage(player, dungeon);
        var currentDamage = dealDamage(ctx, createDamageLogDto, dungeon, newDamage);
        playerRepository.update(ctx, player.addMoney(calculateMoney(player, dungeon)));
        if (currentDamage + newDamage >= dungeon.code().getHealth()) {
          return reviveDungeon(ctx, createDamageLogDto, dungeon);
        }
        return new DungeonStateDto(DungeonState.ALIVE, dungeon.code());
      });
    } catch (DataSourceException e) {
      throw new ApplicationException("Error during attack. Try again");
    }
  }

  private Long dealDamage(TransactionContext ctx, CreateDamageLogDto createDamageLogDto, DungeonEntity dungeon, Integer newDamage) {
    var currentDamage = damageLogRepository.countDamage(ctx, createDamageLogDto.dungeonId());
    if (currentDamage < dungeon.code().getHealth()) {
      damageLogRepository.save(ctx, new DamageLogEntity(createDamageLogDto.playerId(),
        createDamageLogDto.dungeonId(), newDamage));
    }
    return currentDamage;
  }

  private DungeonStateDto reviveDungeon(TransactionContext ctx, CreateDamageLogDto createDamageLogDto, DungeonEntity dungeon) {
    var activePlayers = damageLogRepository.findPlayersForActiveDungeon(ctx,
      createDamageLogDto.dungeonId());
    playerRepository.updateLevelsForIds(ctx, activePlayers);
    damageLogRepository.revive(ctx, createDamageLogDto.dungeonId());
    return new DungeonStateDto(DungeonState.COMPLETED, dungeon.code());
  }

  private Integer calculateDamage(PlayerEntity playerEntity, DungeonEntity dungeon) {
    return Math.max(playerEntity.getExperience() * 4 - dungeon.level() * 2, 1);
  }

  private Integer calculateMoney(PlayerEntity playerEntity, DungeonEntity dungeon) {
    return dungeon.level();
  }
}
