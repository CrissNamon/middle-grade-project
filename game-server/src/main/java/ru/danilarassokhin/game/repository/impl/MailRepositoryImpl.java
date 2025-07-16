package ru.danilarassokhin.game.repository.impl;

import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.quota.ClientQuotaAlteration.Op;
import ru.danilarassokhin.game.entity.MailEntity;
import ru.danilarassokhin.game.repository.MailRepository;
import ru.danilarassokhin.sql.service.TransactionManager;
import tech.hiddenproject.progressive.annotation.Autofill;
import tech.hiddenproject.progressive.annotation.GameBean;

@GameBean
@RequiredArgsConstructor(onConstructor_ = {@Autofill})
public class MailRepositoryImpl implements MailRepository {

  private final TransactionManager transactionManager;

  private static final String SAVE_QUERY =
      String.format("INSERT INTO %s(email, text) VALUES (?, ?)", MailEntity.TABLE_NAME);
  private static final String FIND_BY_ID_QUERY =
      String.format("SELECT * FROM %s WHERE id = ?", MailEntity.TABLE_NAME);
  private static final String FIND_ONE_FOR_SEND_QUERY =
      String.format("SELECT * FROM %s LIMIT 1 FOR UPDATE SKIP LOCKED", MailEntity.TABLE_NAME);

  @Override
  public UUID save(MailEntity entity) {
    return transactionManager.fetchInTransaction(ctx -> ctx.query(SAVE_QUERY, entity.email(), entity.text())
        .fetchOne(UUID.class));
  }

  @Override
  public Optional<MailEntity> findById(UUID id) {
    return Optional.ofNullable(
        transactionManager.fetchInTransaction(ctx -> ctx.query(FIND_BY_ID_QUERY, id)
            .fetchOne(MailEntity.class)));
  }

  public Optional<MailEntity> selectOneForSend() {
    return Optional.ofNullable(
        transactionManager.fetchInTransaction(ctx -> ctx.query(FIND_ONE_FOR_SEND_QUERY))
            .fetchOne(MailEntity.class));
  }
}
