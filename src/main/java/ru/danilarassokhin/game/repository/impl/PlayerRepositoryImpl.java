package ru.danilarassokhin.game.repository.impl;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import ru.danilarassokhin.game.entity.PlayerEntity;
import ru.danilarassokhin.game.repository.PlayerRepository;
import ru.danilarassokhin.game.sql.service.TransactionContext;
import tech.hiddenproject.progressive.annotation.Autofill;
import tech.hiddenproject.progressive.annotation.GameBean;

@GameBean
@RequiredArgsConstructor(onConstructor_ = {@Autofill})
public class PlayerRepositoryImpl implements PlayerRepository {

  private static final String SAVE_QUERY =
      String.format("INSERT INTO %s(name) VALUES(?) RETURNING ID;", PlayerEntity.TABLE_NAME);
  private static final String FIND_BY_ID_QUERY =
      String.format("SELECT * FROM %s WHERE id = ?;", PlayerEntity.TABLE_NAME);
  private static final String EXISTS_BY_ID_QUERY =
      String.format("SELECT EXISTS(SELECT 1 FROM %s WHERE id = ?);", PlayerEntity.TABLE_NAME);
  private static final String EXISTS_BY_NAME_QUERY =
      String.format("SELECT EXISTS(SELECT 1 FROM %s WHERE name = ?);", PlayerEntity.TABLE_NAME);

  @Override
  public Integer save(TransactionContext ctx, PlayerEntity entity) {
    return ctx.query(SAVE_QUERY, entity.name()).fetchOne(Integer.class);
  }

  @Override
  public Optional<PlayerEntity> findById(TransactionContext ctx, Integer id) {
    return Optional.ofNullable(ctx.query(FIND_BY_ID_QUERY, id).fetchOne(PlayerEntity.class));
  }

  @Override
  public boolean existsById(TransactionContext ctx, Integer id) {
    return ctx.query(EXISTS_BY_ID_QUERY, id).fetchOne(Boolean.class);
  }

  @Override
  public boolean existsByName(TransactionContext ctx, String name) {
    return ctx.query(EXISTS_BY_NAME_QUERY, name).fetchOne(Boolean.class);
  }
}
