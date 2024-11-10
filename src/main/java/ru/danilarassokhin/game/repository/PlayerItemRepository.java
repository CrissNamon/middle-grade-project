package ru.danilarassokhin.game.repository;

import ru.danilarassokhin.game.entity.PlayerItem;
import ru.danilarassokhin.game.sql.repository.JdbcRepository;

/**
 * Repository for {@link PlayerItem}.
 */
public interface PlayerItemRepository extends JdbcRepository<PlayerItem, Integer> {

}
