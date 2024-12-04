package ru.danilarassokhin.game.repository.impl;

import static ru.danilarassokhin.game.config.CacheConfig.CACHE_NAME_DUNGEON_CATALOGUE;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.danilarassokhin.game.entity.CatalogueDungeonEntity;
import ru.danilarassokhin.game.repository.CatalogueDungeonRepository;
import ru.danilarassokhin.game.resilience.annotation.Cacheable;
import ru.danilarassokhin.game.sql.service.TransactionManager;
import tech.hiddenproject.progressive.annotation.Autofill;
import tech.hiddenproject.progressive.annotation.GameBean;

/**
 * Implementation of {@link CatalogueDungeonRepository}.
 */
@GameBean
@RequiredArgsConstructor(onConstructor_ = {@Autofill})
@Slf4j
public class CatalogueDungeonRepositoryImpl implements CatalogueDungeonRepository {

  private static final String FIND_BY_CODE_QUERY =
      String.format("SELECT * FROM %s WHERE code = ?;", CatalogueDungeonEntity.TABLE_NAME);

  private final TransactionManager transactionManager;

  @Override
  @Cacheable(CACHE_NAME_DUNGEON_CATALOGUE)
  public Optional<CatalogueDungeonEntity> findByCode(String code) {
    return transactionManager.fetchInTransaction(ctx -> Optional.ofNullable(
        ctx.query(FIND_BY_CODE_QUERY, code).fetchOne(CatalogueDungeonEntity.class)));
  }
}
