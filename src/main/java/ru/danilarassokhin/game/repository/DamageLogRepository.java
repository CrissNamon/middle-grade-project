package ru.danilarassokhin.game.repository;

import java.util.List;

import ru.danilarassokhin.game.entity.DamageLogEntity;
import ru.danilarassokhin.game.sql.repository.JdbcRepository;

/**
 * Repository for {@link DamageLogEntity}.
 */
public interface DamageLogRepository extends JdbcRepository<DamageLogEntity, Integer> {

  /**
   * Counts current damage for given dungeon id.
   * @param dungeonId ID of dungeon
   * @return Sum of damage
   */
  Long countDamage(Integer dungeonId);

  /**
   * Revives dungeon with given id.
   * @param dungeonId ID of dungeon
   */
  void revive(Integer dungeonId);

  /**
   * Searches for ids of active players for given dungeon id.
   * @param dungeonId ID of dungeon
   * @return List of player ids
   */
  List<Integer> findPlayersForActiveDungeon(Integer dungeonId);

}
