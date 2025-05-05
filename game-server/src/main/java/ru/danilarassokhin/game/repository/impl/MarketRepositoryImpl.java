package ru.danilarassokhin.game.repository.impl;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import ru.danilarassokhin.game.entity.MarketEntity;
import ru.danilarassokhin.game.repository.MarketRepository;
import ru.danilarassokhin.sql.service.TransactionManager;
import tech.hiddenproject.progressive.annotation.Autofill;
import tech.hiddenproject.progressive.annotation.GameBean;

@GameBean
@RequiredArgsConstructor(onConstructor_ = {@Autofill})
public class MarketRepositoryImpl implements MarketRepository {

  private static final String SAVE_QUERY =
      String.format("""
        INSERT INTO %s(item_code, price, amount) VALUES(?, ?, ?) 
         ON CONFLICT ON CONSTRAINT %s DO UPDATE SET price = EXCLUDED.price, amount = %s.amount + EXCLUDED.amount
         RETURNING ID;""", MarketEntity.TABLE_NAME, MarketEntity.UX_MARKET_ITEM_PRICE_CONSTRAINT, MarketEntity.TABLE_NAME);
  private static final String FIND_BY_ID_QUERY =
      String.format("SELECT * FROM %s WHERE id = ?;", MarketEntity.TABLE_NAME);
  private static final String EXISTS_BY_ID_QUERY =
      String.format("SELECT EXISTS(SELECT 1 FROM %s WHERE id = ?);", MarketEntity.TABLE_NAME);
  private static final String DECREASE_ITEM_QUERY =
      String.format("UPDATE %s SET amount = amount - 1 WHERE id = ?;", MarketEntity.TABLE_NAME);

  private final TransactionManager transactionManager;

  @Override
  public void decreaseItem(Integer itemId) {
    transactionManager.doInTransaction(ctx -> ctx.query(DECREASE_ITEM_QUERY, itemId).execute());
  }

  @Override
  public Integer save(MarketEntity entity) {
    return transactionManager.fetchInTransaction(ctx -> ctx.query(
        SAVE_QUERY, entity.itemCode(), entity.price(), entity.amount()).fetchOne(Integer.class));
  }

  @Override
  public Optional<MarketEntity> findById(Integer id) {
    return transactionManager.fetchInTransaction(ctx -> Optional.ofNullable(
        ctx.query(FIND_BY_ID_QUERY, id).fetchOne(MarketEntity.class)));
  }
}
