package ru.danilarassokhin.game.entity;

import ru.danilarassokhin.sql.annotation.Column;
import ru.danilarassokhin.sql.annotation.Entity;

/**
 * Table for dungeons.
 * @param id Dungeon id
 * @param level Level
 * @param code Dungeon code
 */
@Entity(DungeonEntity.TABLE_NAME)
public record DungeonEntity(
    @Column("id") Integer id,
    @Column("level") Integer level,
    @Column("code") String code
) {

  public static final String TABLE_NAME = "dungeon";

}
