package ru.danilarassokhin.game.entity;

import ru.danilarassokhin.game.entity.data.Dungeon;
import ru.danilarassokhin.game.sql.annotation.Column;
import ru.danilarassokhin.game.sql.annotation.Entity;

/**
 * Table for dungeons.
 * @param id Dungeon id
 * @param level Level
 * @param code {@link Dungeon}
 */
@Entity(DungeonEntity.TABLE_NAME)
public record DungeonEntity(
    @Column("id") Integer id,
    @Column("level") Integer level,
    @Column("code") Dungeon code
) {

  public static final String TABLE_NAME = "dungeon";

}
