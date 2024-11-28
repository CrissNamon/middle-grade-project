package ru.danilarassokhin.game.repository.impl;

import javax.cache.Cache;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.danilarassokhin.game.entity.CatalogueDungeonEntity;
import ru.danilarassokhin.game.repository.CatalogueDungeonRepository;
import ru.danilarassokhin.game.sql.service.TransactionContext;
import tech.hiddenproject.progressive.annotation.Autofill;
import tech.hiddenproject.progressive.annotation.GameBean;
import tech.hiddenproject.progressive.annotation.Qualifier;

@GameBean
@RequiredArgsConstructor(onConstructor_ = {@Autofill})
@Slf4j
public class CatalogueDungeonRepositoryImpl implements CatalogueDungeonRepository {

  private static final String FIND_BY_CODE_QUERY =
      String.format("SELECT * FROM %s WHERE code = ?;", CatalogueDungeonEntity.TABLE_NAME);

  @Qualifier("dungeonCatalogueCache")
  private final Cache<String, CatalogueDungeonEntity> dungeonsByCodeCache;

  @Override
  public Optional<CatalogueDungeonEntity> findByCode(TransactionContext ctx, String code) {
    return Optional.ofNullable(dungeonsByCodeCache.get(code))
        .or(() -> Optional.ofNullable(ctx.query(FIND_BY_CODE_QUERY, code).fetchOne(CatalogueDungeonEntity.class)));
  }
}
