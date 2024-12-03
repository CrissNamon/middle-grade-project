package ru.danilarassokhin.game.repository.impl;

import javax.cache.Cache;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import ru.danilarassokhin.game.entity.CatalogueDungeonEntity;
import ru.danilarassokhin.game.repository.CatalogueDungeonRepository;
import ru.danilarassokhin.game.sql.service.TransactionManager;
import tech.hiddenproject.progressive.annotation.Autofill;
import tech.hiddenproject.progressive.annotation.GameBean;
import tech.hiddenproject.progressive.annotation.Qualifier;

/**
 * Implementation of {@link CatalogueDungeonRepository}.
 */
@GameBean
@RequiredArgsConstructor(onConstructor_ = {@Autofill})
public class CatalogueDungeonRepositoryImpl implements CatalogueDungeonRepository {

  private static final String FIND_BY_CODE_QUERY =
      String.format("SELECT * FROM %s WHERE code = ?;", CatalogueDungeonEntity.TABLE_NAME);

  @Qualifier("dungeonCatalogueCache")
  private final Cache<String, CatalogueDungeonEntity> dungeonsByCodeCache;
  private final TransactionManager transactionManager;

  @Override
  public Optional<CatalogueDungeonEntity> findByCode(String code) {
    return transactionManager.fetchInTransaction(ctx -> Optional.ofNullable(dungeonsByCodeCache.get(code))
        .or(() -> Optional.ofNullable(ctx.query(FIND_BY_CODE_QUERY, code).fetchOne(CatalogueDungeonEntity.class))));
  }
}
