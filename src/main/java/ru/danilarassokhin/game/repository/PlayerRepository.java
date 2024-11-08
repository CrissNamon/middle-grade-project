package ru.danilarassokhin.game.repository;

import ru.danilarassokhin.game.entity.PlayerEntity;
import ru.danilarassokhin.game.sql.repository.JdbcRepository;
import ru.danilarassokhin.game.sql.service.TransactionContext;

public interface PlayerRepository extends JdbcRepository<PlayerEntity, Integer> {

  boolean existsByName(TransactionContext ctx, String name);

}
