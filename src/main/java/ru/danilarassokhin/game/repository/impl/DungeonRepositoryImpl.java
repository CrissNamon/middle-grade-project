package ru.danilarassokhin.game.repository.impl;

import java.util.Optional;

import ru.danilarassokhin.game.entity.DungeonEntity;
import ru.danilarassokhin.game.entity.data.Dungeon;
import ru.danilarassokhin.game.factory.BloomFilterFactory;
import ru.danilarassokhin.game.model.resilience.BloomFilterWithPresence;
import ru.danilarassokhin.game.repository.DungeonRepository;
import ru.danilarassokhin.game.sql.service.TransactionContext;
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

  private final BloomFilterWithPresence<Integer> dungeonIdsBloomFilter;

  @Autofill
  public DungeonRepositoryImpl(BloomFilterFactory bloomFilterFactory) {
    this.dungeonIdsBloomFilter = bloomFilterFactory.create("dungeonIds", Integer.class);
  }

  @Override
  public Integer save(TransactionContext ctx, DungeonEntity entity) {
    var result = ctx.query(SAVE_QUERY, entity.level(), entity.code()).fetchOne(Integer.class);
    dungeonIdsBloomFilter.put(result);
    return result;
  }

  @Override
  public Optional<DungeonEntity> findById(TransactionContext ctx, Integer id) {
    return Optional.ofNullable(ctx.query(FIND_BY_ID_QUERY, id).fetchOne(DungeonEntity.class));
  }

  @Override
  public boolean existsById(TransactionContext ctx, Integer id) {
    return switch (dungeonIdsBloomFilter.mightContain(id)) {
      case UNKNOWN -> {
        var existsById = ctx.query(EXISTS_BY_ID, id).fetchOne(Boolean.class);
        dungeonIdsBloomFilter.acknowledge(id);
        if (existsById) {
          dungeonIdsBloomFilter.put(id);
        }
        yield existsById;
      }
      case NOT_CONTAINS -> false;
      case MIGHT_CONTAINS -> true;
    };
  }

  @Override
  public boolean existsByLevelAndCode(TransactionContext ctx, Integer level, Dungeon code) {
    return ctx.query(EXISTS_BY_LEVEL_AND_CODE, level, code).fetchOne(Boolean.class);
  }
}
