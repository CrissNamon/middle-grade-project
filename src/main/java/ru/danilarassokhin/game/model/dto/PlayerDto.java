package ru.danilarassokhin.game.model.dto;

public record PlayerDto(
    Integer id,
    String name,
    Integer level,
    Integer money,
    Integer experience
) {}
