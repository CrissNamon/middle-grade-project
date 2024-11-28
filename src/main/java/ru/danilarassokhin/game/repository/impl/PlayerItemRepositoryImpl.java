package ru.danilarassokhin.game.repository.impl;

import java.util.Optional;

import ru.danilarassokhin.game.entity.PlayerItem;
import ru.danilarassokhin.game.factory.BloomFilterFactory;
import ru.danilarassokhin.game.model.resilience.BloomFilterWithPresence;
import ru.danilarassokhin.game.repository.PlayerItemRepository;
import ru.danilarassokhin.game.sql.service.TransactionContext;
import tech.hiddenproject.progressive.annotation.Autofill;
import tech.hiddenproject.progressive.annotation.GameBean;

@GameBean
public class PlayerItemRepositoryImpl implements PlayerItemRepository {

  private static final String SAVE_QUERY =
      String.format("""
        INSERT INTO %s(player_id, item_code, amount) VALUES(?, ?, ?) 
        ON CONFLICT ON CONSTRAINT %s DO UPDATE SET amount = %s.amount + EXCLUDED.amount RETURNING ID;
        """, PlayerItem.TABLE_NAME, PlayerItem.UX_PLAYER_ITEM_ID_CONSTRAINT, PlayerItem.TABLE_NAME);
  private static final String FIND_BY_ID_QUERY =
      String.format("SELECT * FROM %s WHERE id = ?;", PlayerItem.TABLE_NAME);
  private static final String EXISTS_BY_ID_QUERY =
      String.format("SELECT EXISTS(SELECT 1 FROM %s WHERE id = ?);", PlayerItem.TABLE_NAME);

  private final BloomFilterWithPresence<Integer> playerItemIdsBloomFilter;

  @Autofill
  public PlayerItemRepositoryImpl(BloomFilterFactory bloomFilterFactory) {
    this.playerItemIdsBloomFilter = bloomFilterFactory.create("playerItemIds", Integer.class);
  }

  @Override
  public Integer save(TransactionContext ctx, PlayerItem entity) {
    var result = ctx.query(SAVE_QUERY, entity.playerId(), entity.item().name(), entity.amount()).fetchOne(Integer.class);
    playerItemIdsBloomFilter.acknowledge(result);
    return result;
  }

  @Override
  public Optional<PlayerItem> findById(TransactionContext ctx, Integer id) {
    return Optional.ofNullable(ctx.query(FIND_BY_ID_QUERY, id).fetchOne(PlayerItem.class));
  }

  @Override
  public boolean existsById(TransactionContext ctx, Integer id) {
    return switch (playerItemIdsBloomFilter.mightContain(id)) {
      case UNKNOWN -> {
        var existsById = ctx.query(EXISTS_BY_ID_QUERY, id).fetchOne(Boolean.class);
        playerItemIdsBloomFilter.acknowledge(id);
        if (existsById) {
          playerItemIdsBloomFilter.put(id);
        }
        yield existsById;
      }
      case NOT_CONTAINS -> false;
      case MIGHT_CONTAINS -> true;
    };
  }
}
