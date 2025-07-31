package ru.danilarassokhin.game.repository;

import java.util.Optional;
import java.util.UUID;

import ru.danilarassokhin.game.entity.MailEntity;
import ru.danilarassokhin.sql.repository.JdbcRepository;
import ru.danilarassokhin.sql.service.TransactionContext;

public interface MailRepository extends JdbcRepository<MailEntity, UUID> {

  Optional<MailEntity> findOneForSend(TransactionContext ctx);

  void markProcessed(MailEntity mailEntity, TransactionContext ctx);

}
