package ru.danilarassokhin.game.entity;

import ru.danilarassokhin.game.sql.annotation.Column;
import ru.danilarassokhin.game.sql.annotation.Entity;

@Entity(CatalogueDungeonEntity.TABLE_NAME)
public record CatalogueDungeonEntity(
    @Column("id") Integer id,
    @Column("code") String code,
    @Column("health") Integer health
) {

  public static final String TABLE_NAME = "catalogue_dungeon";

}
