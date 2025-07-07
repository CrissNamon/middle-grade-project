package ru.danilarassokhin.game.model.request;

import org.hibernate.validator.constraints.Length;

/**
 * DTO for creating new player request.
 * @param name Player name
 */
public record CreatePlayerRequest(
    @Length(min = 4) String name
) {}
