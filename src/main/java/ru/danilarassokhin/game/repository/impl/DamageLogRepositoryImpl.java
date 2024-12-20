package ru.danilarassokhin.game.repository.impl;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import ru.danilarassokhin.game.entity.DamageLogEntity;
import ru.danilarassokhin.game.repository.DamageLogRepository;
import ru.danilarassokhin.game.sql.service.TransactionManager;
import tech.hiddenproject.progressive.annotation.Autofill;
import tech.hiddenproject.progressive.annotation.GameBean;

@GameBean
@RequiredArgsConstructor(onConstructor_ = {@Autofill})
public class DamageLogRepositoryImpl implements DamageLogRepository {

  private final String SAVE_QUERY =
      String.format("INSERT INTO %s(dungeon_id, player_id, damage) VALUES(?, ?, ?) RETURNING id;", DamageLogEntity.TABLE_NAME);
  private final String FIND_BY_ID_QUERY =
      String.format("SELECT * FROM %s WHERE id = ?;", DamageLogEntity.TABLE_NAME);
  private final String EXISTS_BY_ID_QUERY =
      String.format("SELECT EXISTS(SELECT 1 FROM %s WHERE id = ?);", DamageLogEntity.TABLE_NAME);
  private final String COUNT_DMG_QUERY =
      String.format("SELECT COALESCE(SUM(damage), 0) FROM %s WHERE dungeon_id = ? AND active = true;", DamageLogEntity.TABLE_NAME);
  private final String REVIVE_QUERY =
      String.format("UPDATE %s SET active = false WHERE dungeon_id = ? AND active = true;", DamageLogEntity.TABLE_NAME);
  private final String FIND_ACTIVE_PLAYERS =
      String.format("SELECT DISTINCT player_id FROM %s WHERE dungeon_id = ? AND active = true;", DamageLogEntity.TABLE_NAME);

  private final TransactionManager transactionManager;

  @Override
  public Integer save(DamageLogEntity entity) {
    return transactionManager.fetchInTransaction(ctx -> ctx.query(SAVE_QUERY, entity.dungeonId(), entity.playerId(), entity.damage())
        .fetchOne(Integer.class));
  }

  @Override
  public Optional<DamageLogEntity> findById(Integer id) {
    return transactionManager.fetchInTransaction(ctx -> Optional.ofNullable(
        ctx.query(FIND_BY_ID_QUERY, id).fetchOne(DamageLogEntity.class)));
  }

  @Override
  public List<Integer> findPlayersForActiveDungeon(Integer dungeonId) {
    return transactionManager.fetchInTransaction(ctx -> ctx.query(FIND_ACTIVE_PLAYERS, dungeonId).fetchInto(Integer.class));
  }

  @Override
  public Long countDamage(Integer dungeonId) {
    return transactionManager.fetchInTransaction(ctx -> ctx.query(COUNT_DMG_QUERY, dungeonId).fetchOne(Long.class));
  }

  @Override
  public void revive(Integer dungeonId) {
    transactionManager.doInTransaction(ctx -> ctx.query(REVIVE_QUERY, dungeonId).execute());
  }
}
