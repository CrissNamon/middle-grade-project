package ru.danilarassokhin.game.repository.impl;

import java.util.Optional;

import ru.danilarassokhin.game.entity.MarketEntity;
import ru.danilarassokhin.game.repository.MarketRepository;
import ru.danilarassokhin.game.sql.service.TransactionContext;
import tech.hiddenproject.progressive.annotation.GameBean;

@GameBean
public class MarketRepositoryImpl implements MarketRepository {

  private static final String SAVE_QUERY =
      String.format("INSERT INTO %s(item_code, price, amount) VALUES(?, ?, ?) RETURNING ID;", MarketEntity.TABLE_NAME);
  private static final String FIND_BY_ID_QUERY =
      String.format("SELECT * FROM %s WHERE id = ?;", MarketEntity.TABLE_NAME);
  private static final String EXISTS_BY_ID_QUERY =
      String.format("SELECT EXISTS(SELECT 1 FROM %s WHERE id = ?);", MarketEntity.TABLE_NAME);
  private static final String DECREASE_ITEM_QUERY =
      String.format("UPDATE %s SET amount = amount - 1 WHERE id = ?;", MarketEntity.TABLE_NAME);

  @Override
  public void decreaseItem(TransactionContext ctx, Integer itemId) {
    ctx.query(DECREASE_ITEM_QUERY, itemId).execute();
  }

  @Override
  public Integer save(TransactionContext ctx, MarketEntity entity) {
    return ctx.query(SAVE_QUERY, entity.itemCode(), entity.price(), entity.amount()).fetchOne(Integer.class);
  }

  @Override
  public Optional<MarketEntity> findById(TransactionContext ctx, Integer id) {
    return Optional.ofNullable(ctx.query(FIND_BY_ID_QUERY, id).fetchOne(MarketEntity.class));
  }

  @Override
  public boolean existsById(TransactionContext ctx, Integer id) {
    return ctx.query(EXISTS_BY_ID_QUERY, id).fetchOne(Boolean.class);
  }
}
