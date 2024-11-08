package ru.danilarassokhin.game.entity;

import ru.danilarassokhin.game.sql.annotation.Column;
import ru.danilarassokhin.game.sql.annotation.Entity;

@Entity(PlayerEntity.TABLE_NAME)
public record PlayerEntity(
    @Column("id") Integer id,
    @Column("name") String name,
    @Column("level") Integer level,
    @Column("money") Integer money,
    @Column("experience") Integer experience
) {

  public static final String TABLE_NAME = "player";

}
