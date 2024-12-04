package ru.danilarassokhin.game.entity;

import java.io.Serializable;

import ru.danilarassokhin.game.sql.annotation.Column;
import ru.danilarassokhin.game.sql.annotation.Entity;

/**
 * Dungeon data.
 * @param id Unique ID
 * @param code Unique code
 * @param health Initial health
 */
@Entity(CatalogueDungeonEntity.TABLE_NAME)
public record CatalogueDungeonEntity(
    @Column("id") Integer id,
    @Column("code") String code,
    @Column("health") Integer health
) implements Serializable {

  public static final String TABLE_NAME = "catalogue_dungeon";

}
