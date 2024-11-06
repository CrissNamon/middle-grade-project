package ru.danilarassokhin.game.entity;

import ru.danilarassokhin.game.sql.annotation.Column;
import ru.danilarassokhin.game.sql.annotation.Entity;

@Entity("player")
public record PlayerEntity(
    @Column("id") Integer id,
    @Column("name") String name,
    @Column("money") Integer money,
    @Column("experience") Integer experience
) {}
