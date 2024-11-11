package ru.danilarassokhin.game.model.dto;

/**
 * DTO for player entity.
 * @param id ID of player
 * @param name Player name
 * @param level Player level
 * @param money Player money
 * @param experience Player experience
 */
public record PlayerDto(
    Integer id,
    String name,
    Integer level,
    Integer money,
    Integer experience
) {}
