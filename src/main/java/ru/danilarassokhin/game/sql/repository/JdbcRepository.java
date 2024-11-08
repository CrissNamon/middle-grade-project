package ru.danilarassokhin.game.sql.repository;

import java.sql.SQLException;
import java.util.Optional;

import ru.danilarassokhin.game.sql.service.TransactionContext;

public interface JdbcRepository<E, I> {

  I save(TransactionContext transactionContext, E entity) throws SQLException;

  Optional<E> findById(TransactionContext transactionContext, I id);

  boolean existsById(TransactionContext transactionContext, I id);

}
