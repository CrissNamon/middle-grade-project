package ru.danilarassokhin.game.model.request;

import jakarta.validation.constraints.Min;
import ru.danilarassokhin.game.entity.data.Dungeon;

/**
 * DTO for creating new dungeon request.
 * @param level Dungeon level
 * @param code {@link Dungeon}
 */
public record CreateDungeonRequest(
    @Min(1) Integer level,
    Dungeon code
) {}
