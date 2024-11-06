package ru.danilarassokhin.game.repository.impl;

import lombok.RequiredArgsConstructor;
import ru.danilarassokhin.game.entity.PlayerEntity;
import ru.danilarassokhin.game.repository.PlayerRepository;
import ru.danilarassokhin.game.sql.service.TransactionManager;
import tech.hiddenproject.progressive.annotation.Autofill;
import tech.hiddenproject.progressive.annotation.GameBean;

@GameBean
@RequiredArgsConstructor(onConstructor_ = {@Autofill})
public class PlayerRepositoryImpl implements PlayerRepository {

  private final TransactionManager transactionManager;

  @Override
  public boolean existsByName(String name) {
    return transactionManager.fetchInTransaction(trx -> trx.fetchOne("SELECT EXISTS(SELECT * FROM player WHERE name = ?);", Boolean.class, name));
  }

  @Override
  public void save(PlayerEntity playerEntity) {
    transactionManager.doInTransaction(trx -> trx.executeUpdate("INSERT INTO player(name) VALUES(?);", playerEntity.name()));
  }
}
