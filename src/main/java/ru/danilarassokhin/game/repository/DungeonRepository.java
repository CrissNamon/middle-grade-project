package ru.danilarassokhin.game.repository;

import ru.danilarassokhin.game.entity.DungeonEntity;
import ru.danilarassokhin.game.entity.data.Dungeon;
import ru.danilarassokhin.game.sql.repository.JdbcRepository;
import ru.danilarassokhin.game.sql.service.TransactionContext;

public interface DungeonRepository extends JdbcRepository<DungeonEntity, Integer> {

  boolean existsByLevelAndCode(TransactionContext ctx, Integer level, Dungeon code);

}
