package ru.danilarassokhin.game.service.impl;

import static ru.danilarassokhin.game.config.DataSourceConfig.TRANSACTION_DEFAULT_RETRY_COUNT;

import java.sql.Connection;
import java.sql.SQLException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.danilarassokhin.game.entity.CatalogueDungeonEntity;
import ru.danilarassokhin.game.entity.DamageLogEntity;
import ru.danilarassokhin.game.entity.DungeonEntity;
import ru.danilarassokhin.game.entity.PlayerEntity;
import ru.danilarassokhin.game.entity.camunda.CamundaSignal;
import ru.danilarassokhin.game.entity.data.DungeonState;
import ru.danilarassokhin.game.mapper.DungeonMapper;
import ru.danilarassokhin.game.model.dto.CreateDamageLogDto;
import ru.danilarassokhin.game.model.dto.CreateDungeonDto;
import ru.danilarassokhin.game.model.dto.DungeonDto;
import ru.danilarassokhin.game.model.dto.DungeonStateDto;
import ru.danilarassokhin.game.repository.CamundaRepository;
import ru.danilarassokhin.game.repository.CatalogueDungeonRepository;
import ru.danilarassokhin.game.repository.DamageLogRepository;
import ru.danilarassokhin.game.repository.DungeonRepository;
import ru.danilarassokhin.game.repository.PlayerRepository;
import ru.danilarassokhin.game.service.DungeonService;
import ru.danilarassokhin.injection.exception.ApplicationException;
import ru.danilarassokhin.sql.annotation.Transactional;
import ru.danilarassokhin.game.util.AwaitUtil;
import ru.danilarassokhin.sql.exception.DataSourceException;
import tech.hiddenproject.progressive.annotation.Autofill;
import tech.hiddenproject.progressive.annotation.GameBean;

@GameBean
@RequiredArgsConstructor(onConstructor_ = {@Autofill})
@Slf4j
public class DungeonServiceImpl implements DungeonService {

  private static final Integer MINIMUM_DAMAGE_COUNT = 1;

  private final DungeonMapper dungeonMapper;
  private final DungeonRepository dungeonRepository;
  private final DamageLogRepository damageLogRepository;
  private final PlayerRepository playerRepository;
  private final CamundaRepository camundaRepository;
  private final CatalogueDungeonRepository catalogueDungeonRepository;

  @Override
  @Transactional
  public DungeonDto save(CreateDungeonDto createDungeonDto) {
    if (!dungeonRepository.existsByLevelAndCode(createDungeonDto.level(), createDungeonDto.code())) {
      var id = dungeonRepository.save(dungeonMapper.createDungeonDtoToEntity(createDungeonDto));
      return dungeonRepository.findById(id)
          .map(dungeonMapper::dungeonEntityToDto)
          .orElseThrow(() -> new ApplicationException("Dungeon save failed"));
    }
    throw new ApplicationException("Dungeon already exists");
  }

  //Используем SERIALIZABLE, т.к. транзакция выполняет аггрегацию и вставку новых строк в таблицу
  //Несколько игроков атакуют одного монстра => строки с уроном должны вставляться строго последовательно
  //Т.е. нужна полная изоляция
  //REPEATABLE READ обеспечивает изоляцию только для изменения строк несколькими транзакциями
  //Т.к. в Postgres SERIALIZABLE использует предикатные блокировки, транзакция может упасть с ошибкой сериализации
  //Повторяем транзакцию пока не выполнится успешно, но не более 10 раз
  @Override
  @Transactional(isolationLevel = Connection.TRANSACTION_SERIALIZABLE)
  public DungeonStateDto attack(CreateDamageLogDto createDamageLogDto) {
    return AwaitUtil.retryOnError(TRANSACTION_DEFAULT_RETRY_COUNT,
                                  () -> attackDungeonTransaction(createDamageLogDto),
                                  () -> log.warn("Transaction attack(CreateDamageLogDto) failed. Retrying..."),
                                  DataSourceException.class, SQLException.class);
  }

  @Override
  public DungeonDto findByLevel(Integer level) {
    return dungeonRepository.findByLevel(level)
        .map(dungeonMapper::dungeonEntityToDto)
        .orElseThrow(() -> new ApplicationException("Dungeon not found"));
  }

  private DungeonStateDto attackDungeonTransaction(CreateDamageLogDto createDamageLogDto) {
    try {
      var dungeon = dungeonRepository.findById(createDamageLogDto.dungeonId())
          .orElseThrow(() -> new ApplicationException("Dungeon not found"));
      var player = playerRepository.findById(createDamageLogDto.playerId())
          .orElseThrow(() -> new ApplicationException("Player not found"));
      var dungeonCatalogue = catalogueDungeonRepository.findByCode(dungeon.code())
          .orElseThrow(() -> new ApplicationException("Dungeon not found"));
      var newDamage = calculateDamage(player, dungeon);
      var currentDamage = dealDamage(createDamageLogDto, dungeonCatalogue, newDamage);
      playerRepository.update(player.addMoney(calculateMoney(player, dungeon)));
      if (currentDamage + newDamage >= dungeonCatalogue.health()) {
        return reviveDungeon(createDamageLogDto, dungeon, player.getLevel());
      }
      return new DungeonStateDto(DungeonState.ALIVE, dungeon.code());
    } catch (DataSourceException e) {
      throw new ApplicationException("Error during attack. Try again");
    }
  }

  private Long dealDamage(
      CreateDamageLogDto createDamageLogDto,
      CatalogueDungeonEntity catalogueDungeon,
      Integer newDamage
  ) {
    var currentDamage = damageLogRepository.countDamage(createDamageLogDto.dungeonId());
    if (currentDamage < catalogueDungeon.health()) {
      damageLogRepository.save(new DamageLogEntity(createDamageLogDto.playerId(),
        createDamageLogDto.dungeonId(), newDamage));
    }
    return currentDamage;
  }

  private DungeonStateDto reviveDungeon(
      CreateDamageLogDto createDamageLogDto,
      DungeonEntity dungeon,
      Integer level
  ) {
    var activePlayers = damageLogRepository.findPlayersForActiveDungeon(createDamageLogDto.dungeonId());
    playerRepository.updateLevelsForIds(activePlayers);
    damageLogRepository.revive(createDamageLogDto.dungeonId());
    camundaRepository.broadcastSignal(CamundaSignal.s_dungeon_completed.create(level));
    return new DungeonStateDto(DungeonState.COMPLETED, dungeon.code());
  }

  private Integer calculateDamage(PlayerEntity playerEntity, DungeonEntity dungeon) {
    return Math.max(playerEntity.getExperience() * 4 - dungeon.level() * 2, MINIMUM_DAMAGE_COUNT);
  }

  private Integer calculateMoney(PlayerEntity playerEntity, DungeonEntity dungeon) {
    return dungeon.level();
  }
}
