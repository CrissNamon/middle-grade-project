package ru.danilarassokhin.game.repository;

import java.util.List;

import ru.danilarassokhin.game.entity.PlayerEntity;
import ru.danilarassokhin.sql.repository.JdbcRepository;

/**
 * Repository for {@link PlayerEntity}
 */
public interface PlayerRepository extends JdbcRepository<PlayerEntity, Integer> {

  /**
   * Checks if player with given name exists.
   * @param name Player's name
   * @return true if player with given name exists
   */
  boolean existsByName(String name);

  /**
   * Updates player.
   * @param playerEntity {@link PlayerEntity}
   */
  void update(PlayerEntity playerEntity);

  /**
   * Updates levels for players with given ids.
   * @param playerIds Ids of players for update
   */
  void updateLevelsForIds(List<Integer> playerIds);

}
