package ru.danilarassokhin.game.entity;

import java.util.UUID;

import ru.danilarassokhin.game.service.annotation.Column;
import ru.danilarassokhin.game.service.annotation.Entity;

@Entity("users")
public record UserEntity(
    @Column("id") UUID id,
    @Column("name") String name,
    @Column("experience") Double experience
) {}
