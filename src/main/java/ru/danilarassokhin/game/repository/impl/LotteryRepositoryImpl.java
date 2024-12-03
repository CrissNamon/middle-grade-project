package ru.danilarassokhin.game.repository.impl;

import lombok.RequiredArgsConstructor;
import ru.danilarassokhin.game.entity.PlayerEntity;
import ru.danilarassokhin.game.repository.LotteryRepository;
import ru.danilarassokhin.game.sql.service.TransactionContext;
import ru.danilarassokhin.game.sql.service.TransactionManager;
import tech.hiddenproject.progressive.annotation.Autofill;
import tech.hiddenproject.progressive.annotation.GameBean;

@GameBean
@RequiredArgsConstructor(onConstructor_ = {@Autofill})
public class LotteryRepositoryImpl implements LotteryRepository {

  private static final String TABLE_NAME = "lottery";
  private static final String INSERT_QUERY =
      String.format("INSERT INTO %s(player_id) VALUES(?);", TABLE_NAME);
  private static final String EXISTS_QUERY =
      String.format("SELECT EXISTS(SELECT 1 FROM %s WHERE player_id = ?);", TABLE_NAME);
  private static final String COUNT_QUERY =
      String.format("SELECT COUNT(player_id) FROM %s;", TABLE_NAME);

  private final TransactionManager transactionManager;

  @Override
  public void addPlayer(PlayerEntity playerEntity) {
    transactionManager.doInTransaction(ctx -> ctx.query(INSERT_QUERY, playerEntity.getId()).execute());
  }

  @Override
  public boolean existsByPlayer(PlayerEntity playerEntity) {
    return transactionManager.fetchInTransaction(ctx -> ctx.query(EXISTS_QUERY, playerEntity.getId()).fetchOne(Boolean.class));
  }

  @Override
  public Long countPlayers() {
    return transactionManager.fetchInTransaction(ctx -> ctx.query(COUNT_QUERY).fetchOne(Long.class));
  }
}
