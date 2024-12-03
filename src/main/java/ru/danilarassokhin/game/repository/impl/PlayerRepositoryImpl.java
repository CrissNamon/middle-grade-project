package ru.danilarassokhin.game.repository.impl;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.danilarassokhin.game.entity.PlayerEntity;
import ru.danilarassokhin.game.factory.BloomFilterFactory;
import ru.danilarassokhin.game.model.resilience.BloomFilterWithPresence;
import ru.danilarassokhin.game.repository.PlayerRepository;
import ru.danilarassokhin.game.sql.service.TransactionContext;
import ru.danilarassokhin.game.sql.service.TransactionManager;
import tech.hiddenproject.progressive.annotation.Autofill;
import tech.hiddenproject.progressive.annotation.GameBean;

@GameBean
@Slf4j
public class PlayerRepositoryImpl implements PlayerRepository {

  private static final String SAVE_QUERY =
      String.format("INSERT INTO %s(name) VALUES(?) RETURNING ID;", PlayerEntity.TABLE_NAME);
  private static final String FIND_BY_ID_QUERY =
      String.format("SELECT * FROM %s WHERE id = ?;", PlayerEntity.TABLE_NAME);
  private static final String EXISTS_BY_ID_QUERY =
      String.format("SELECT EXISTS(SELECT 1 FROM %s WHERE id = ?);", PlayerEntity.TABLE_NAME);
  private static final String EXISTS_BY_NAME_QUERY =
      String.format("SELECT EXISTS(SELECT 1 FROM %s WHERE name = ?);", PlayerEntity.TABLE_NAME);
  private static final String UPDATE_QUERY =
      String.format("UPDATE %s SET money = ?, level = ?, experience = ? WHERE id = ?;", PlayerEntity.TABLE_NAME);
  private static final String UPDATE_LEVEL_QUERY =
      String.format("UPDATE %s SET level = level + 1 WHERE id = ANY (?);", PlayerEntity.TABLE_NAME);

  private final BloomFilterWithPresence<String> playerNamesBloomFilter;
  private final BloomFilterWithPresence<Integer> playerIdsBloomFilter;
  private final TransactionManager transactionManager;

  @Autofill
  public PlayerRepositoryImpl(BloomFilterFactory bloomFilterFactory, TransactionManager transactionManager) {
    this.playerNamesBloomFilter = bloomFilterFactory.create("playerNames", String.class);
    this.playerIdsBloomFilter = bloomFilterFactory.create("playerIds", Integer.class);
    this.transactionManager = transactionManager;
  }

  @Override
  public Integer save(TransactionContext ctx, PlayerEntity entity) {
    var result = ctx.query(SAVE_QUERY, entity.getName()).fetchOne(Integer.class);
    playerNamesBloomFilter.put(entity.getName());
    playerIdsBloomFilter.put(result);
    return result;
  }

  @Override
  public Optional<PlayerEntity> findById(TransactionContext ctx, Integer id) {
    playerIdsBloomFilter.acknowledge(id);
    return Optional.ofNullable(ctx.query(FIND_BY_ID_QUERY, id).fetchOne(PlayerEntity.class));
  }

  @Override
  public boolean existsById(TransactionContext ctx, Integer id) {
    return switch (playerIdsBloomFilter.mightContain(id)) {
      case UNKNOWN, MIGHT_CONTAINS -> {
        var existsById = ctx.query(EXISTS_BY_ID_QUERY, id).fetchOne(Boolean.class);
        playerIdsBloomFilter.acknowledge(id);
        if (existsById) {
          playerIdsBloomFilter.put(id);
        }
        yield existsById;
      }
      case NOT_CONTAINS -> false;
    };
  }

  @Override
  public boolean existsByName(TransactionContext ctx, String name) {
    return switch (playerNamesBloomFilter.mightContain(name)) {
      case UNKNOWN -> {
        log.info("Player name not found in filter");
        var existsByName = ctx.query(EXISTS_BY_NAME_QUERY, name).fetchOne(Boolean.class);
        playerNamesBloomFilter.acknowledge(name);
        if (existsByName) {
          playerNamesBloomFilter.put(name);
        }
        yield existsByName;
      }
      case NOT_CONTAINS -> false;
      case MIGHT_CONTAINS -> true;
    };
  }

  @Override
  public void update(TransactionContext ctx, PlayerEntity playerEntity) {
    ctx.query(UPDATE_QUERY, playerEntity.getMoney(), playerEntity.getLevel(),
              playerEntity.getExperience(), playerEntity.getId()).execute();
  }

  @Override
  public void updateLevelsForIds(TransactionContext ctx, List<Integer> playerIds) {
    ctx.query(UPDATE_LEVEL_QUERY, playerIds).execute();
  }

  @Override
  public List<PlayerEntity> findAll() {
    return transactionManager.fetchInTransaction(ctx -> ctx.query("SELECT * FROM player;").fetchInto(PlayerEntity.class));
  }
}
