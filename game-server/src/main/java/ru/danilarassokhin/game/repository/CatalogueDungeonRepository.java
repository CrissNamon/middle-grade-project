package ru.danilarassokhin.game.repository;

import java.util.Optional;

import ru.danilarassokhin.game.entity.CatalogueDungeonEntity;

/**
 * Repository for {@link CatalogueDungeonEntity}.
 */
public interface CatalogueDungeonRepository {

  /**
   * Searches dungeon data by code.
   * @param code Dungeon code
   * @return Optional of {@link CatalogueDungeonEntity}
   */
  Optional<CatalogueDungeonEntity> findByCode(String code);

}
