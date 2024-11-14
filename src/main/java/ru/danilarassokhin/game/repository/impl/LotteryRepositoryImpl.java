package ru.danilarassokhin.game.repository.impl;

import ru.danilarassokhin.game.entity.PlayerEntity;
import ru.danilarassokhin.game.repository.LotteryRepository;
import ru.danilarassokhin.game.sql.service.TransactionContext;
import tech.hiddenproject.progressive.annotation.GameBean;

@GameBean
public class LotteryRepositoryImpl implements LotteryRepository {

  private static final String TABLE_NAME = "lottery";
  private static final String INSERT_QUERY =
      String.format("INSERT INTO %s(player_id) VALUES(?);", TABLE_NAME);
  private static final String EXISTS_QUERY =
      String.format("SELECT EXISTS(SELECT 1 FROM %s WHERE player_id = ?);", TABLE_NAME);
  private static final String COUNT_QUERY =
      String.format("SELECT COUNT(player_id) FROM %s;", TABLE_NAME);

  @Override
  public void addPlayer(TransactionContext ctx, PlayerEntity playerEntity) {
    ctx.query(INSERT_QUERY, playerEntity.getId()).execute();
  }

  @Override
  public boolean existsByPlayer(TransactionContext ctx, PlayerEntity playerEntity) {
    return ctx.query(EXISTS_QUERY, playerEntity.getId()).fetchOne(Boolean.class);
  }

  @Override
  public Long countPlayers(TransactionContext ctx) {
    return ctx.query(COUNT_QUERY).fetchOne(Long.class);
  }
}
