package ru.danilarassokhin.game.repository.impl;

import java.util.Objects;
import java.util.Optional;

import ru.danilarassokhin.game.entity.DungeonEntity;
import ru.danilarassokhin.resilience.factory.BloomFilterFactory;
import ru.danilarassokhin.resilience.model.BloomFilterWithPresence;
import ru.danilarassokhin.game.repository.DungeonRepository;
import ru.danilarassokhin.sql.service.TransactionManager;
import tech.hiddenproject.progressive.annotation.Autofill;
import tech.hiddenproject.progressive.annotation.GameBean;

@GameBean
public class DungeonRepositoryImpl implements DungeonRepository {

  private static final String SAVE_QUERY =
      String.format("INSERT INTO %s(level, code) VALUES(?, ?) RETURNING level;", DungeonEntity.TABLE_NAME);
  private static final String FIND_BY_ID_QUERY =
      String.format("SELECT * FROM %s WHERE id = ?;", DungeonEntity.TABLE_NAME);
  private static final String EXISTS_BY_ID =
      String.format("SELECT EXISTS(SELECT * FROM %s WHERE id = ?);", DungeonEntity.TABLE_NAME);
  private static final String EXISTS_BY_LEVEL_AND_CODE =
      String.format("SELECT EXISTS(SELECT * FROM %s WHERE level = ? AND code = ?);", DungeonEntity.TABLE_NAME);
  private static final String FIND_BY_LEVEL_QUERY =
      String.format("SELECT * FROM %s WHERE level = ?;", DungeonEntity.TABLE_NAME);

  private final BloomFilterWithPresence<Integer> dungeonIdsBloomFilter;
  private final TransactionManager transactionManager;

  @Autofill
  public DungeonRepositoryImpl(BloomFilterFactory bloomFilterFactory, TransactionManager transactionManager) {
    this.dungeonIdsBloomFilter = bloomFilterFactory.create("dungeonIds", Integer.class);
    this.transactionManager = transactionManager;
  }

  @Override
  public Integer save(DungeonEntity entity) {
    var result = transactionManager.fetchInTransaction(ctx -> ctx.query(SAVE_QUERY, entity.level(), entity.code()).fetchOne(Integer.class));
    dungeonIdsBloomFilter.put(result);
    return result;
  }

  @Override
  public Optional<DungeonEntity> findById( Integer id) {
    var result = transactionManager.fetchInTransaction(ctx -> ctx.query(FIND_BY_ID_QUERY, id).fetchOne(DungeonEntity.class));
    if (Objects.nonNull(result)) {
      dungeonIdsBloomFilter.put(result.id());
    } else {
      dungeonIdsBloomFilter.acknowledge(id);
    }
    return Optional.ofNullable(result);
  }

  @Override
  public boolean existsByLevelAndCode(Integer level, String code) {
    return transactionManager.fetchInTransaction(ctx -> ctx.query(EXISTS_BY_LEVEL_AND_CODE, level, code).fetchOne(Boolean.class));
  }

  @Override
  public Optional<DungeonEntity> findByLevel(Integer level) {
    return transactionManager.fetchInTransaction(ctx -> Optional.ofNullable(ctx.query(FIND_BY_LEVEL_QUERY, level).fetchOne(DungeonEntity.class)));
  }
}
