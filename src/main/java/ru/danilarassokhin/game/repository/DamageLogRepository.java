package ru.danilarassokhin.game.repository;

import java.util.List;

import ru.danilarassokhin.game.entity.DamageLogEntity;
import ru.danilarassokhin.game.sql.repository.JdbcRepository;
import ru.danilarassokhin.game.sql.service.TransactionContext;

public interface DamageLogRepository extends JdbcRepository<DamageLogEntity, Integer> {

  Long countDamage(TransactionContext ctx, Integer dungeonId);

  void revive(TransactionContext ctx, Integer dungeonId);

  List<Integer> findPlayersForActiveDungeon(TransactionContext ctx, Integer dungeonId);

}
