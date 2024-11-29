package ru.danilarassokhin.game.repository;

import java.util.Optional;

import ru.danilarassokhin.game.entity.CatalogueDungeonEntity;
import ru.danilarassokhin.game.sql.service.TransactionContext;

/**
 * Repository for {@link CatalogueDungeonEntity}.
 */
public interface CatalogueDungeonRepository {

  /**
   * Searches dungeon data by code.
   * @param ctx {@link TransactionContext}
   * @param code Dungeon code
   * @return Optional of {@link CatalogueDungeonEntity}
   */
  Optional<CatalogueDungeonEntity> findByCode(TransactionContext ctx, String code);

}
