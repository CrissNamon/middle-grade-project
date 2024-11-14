package ru.danilarassokhin.game.entity;

import java.time.LocalDateTime;

import ru.danilarassokhin.game.sql.annotation.Column;
import ru.danilarassokhin.game.sql.annotation.Entity;

/**
 * Table for damage log entries.
 * @param id Log id
 * @param playerId Player id
 * @param dungeonId Dungeon id
 * @param dateTime Time of creation
 * @param active Is log active
 * @param damage Damage count
 */
@Entity(DamageLogEntity.TABLE_NAME)
public record DamageLogEntity(
    @Column("id") Integer id,
    @Column("player_id") Integer playerId,
    @Column("dungeon_id") Integer dungeonId,
    @Column("dateTime") LocalDateTime dateTime,
    @Column("active") Boolean active,
    @Column("damage") Integer damage
) {

  public DamageLogEntity(Integer playerId, Integer dungeonId, Integer damage) {
    this(null, playerId, dungeonId, null, null, damage);
  }

  public static final String TABLE_NAME = "damage_log";

}
