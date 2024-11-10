package ru.danilarassokhin.game.repository.impl;

import java.util.Optional;

import ru.danilarassokhin.game.entity.PlayerItem;
import ru.danilarassokhin.game.repository.PlayerItemRepository;
import ru.danilarassokhin.game.sql.service.TransactionContext;
import tech.hiddenproject.progressive.annotation.GameBean;

@GameBean
public class PlayerItemRepositoryImpl implements PlayerItemRepository {

  private static final String SAVE_QUERY =
      String.format("""
        INSERT INTO %s(player_id, item_code, amount) VALUES(?, ?, ?) 
        ON CONFLICT ON CONSTRAINT %s DO UPDATE SET amount = EXCLUDED.amount + ? RETURNING ID;
        """, PlayerItem.TABLE_NAME, PlayerItem.UX_PLAYER_ITEM_ID_CONSTRAINT);
  private static final String FIND_BY_ID_QUERY =
      String.format("SELECT * FROM %s WHERE id = ?;", PlayerItem.TABLE_NAME);
  private static final String EXISTS_BY_ID_QUERY =
      String.format("SELECT EXISTS(SELECT 1 FROM %s WHERE id = ?);", PlayerItem.TABLE_NAME);

  @Override
  public Integer save(TransactionContext ctx, PlayerItem entity) {
    return ctx.query(SAVE_QUERY, entity.playerId(), entity.item().name(), entity.amount(),
                     entity.amount()).fetchOne(Integer.class);
  }

  @Override
  public Optional<PlayerItem> findById(TransactionContext ctx, Integer id) {
    return Optional.ofNullable(ctx.query(FIND_BY_ID_QUERY, id).fetchOne(PlayerItem.class));
  }

  @Override
  public boolean existsById(TransactionContext ctx, Integer id) {
    return ctx.query(EXISTS_BY_ID_QUERY, id).fetchOne(Boolean.class);
  }
}
