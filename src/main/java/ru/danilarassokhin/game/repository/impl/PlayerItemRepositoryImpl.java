package ru.danilarassokhin.game.repository.impl;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import ru.danilarassokhin.game.entity.PlayerItem;
import ru.danilarassokhin.game.repository.PlayerItemRepository;
import ru.danilarassokhin.game.sql.service.TransactionManager;
import tech.hiddenproject.progressive.annotation.Autofill;
import tech.hiddenproject.progressive.annotation.GameBean;

@GameBean
@RequiredArgsConstructor(onConstructor_ = {@Autofill})
public class PlayerItemRepositoryImpl implements PlayerItemRepository {

  private static final String SAVE_QUERY =
      String.format("""
        INSERT INTO %s(player_id, item_code, amount) VALUES(?, ?, ?) 
        ON CONFLICT ON CONSTRAINT %s DO UPDATE SET amount = %s.amount + EXCLUDED.amount RETURNING ID;
        """, PlayerItem.TABLE_NAME, PlayerItem.UX_PLAYER_ITEM_ID_CONSTRAINT, PlayerItem.TABLE_NAME);
  private static final String FIND_BY_ID_QUERY =
      String.format("SELECT * FROM %s WHERE id = ?;", PlayerItem.TABLE_NAME);

  private final TransactionManager transactionManager;

  @Override
  public Integer save(PlayerItem entity) {
    return transactionManager.fetchInTransaction(ctx -> ctx.query(
        SAVE_QUERY, entity.playerId(), entity.item().name(), entity.amount()).fetchOne(Integer.class));
  }

  @Override
  public Optional<PlayerItem> findById(Integer id) {
    return transactionManager.fetchInTransaction(ctx -> Optional.ofNullable(
        ctx.query(FIND_BY_ID_QUERY, id).fetchOne(PlayerItem.class)));
  }
}
