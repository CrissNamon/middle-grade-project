package ru.danilarassokhin.game.repository;

import java.util.List;

import ru.danilarassokhin.game.entity.PlayerEntity;
import ru.danilarassokhin.game.sql.repository.JdbcRepository;
import ru.danilarassokhin.game.sql.service.TransactionContext;

public interface PlayerRepository extends JdbcRepository<PlayerEntity, Integer> {

  boolean existsByName(TransactionContext ctx, String name);

  void update(TransactionContext ctx, PlayerEntity playerEntity);

  void updateLevelsForIds(TransactionContext ctx, List<Integer> playerIds);

}
