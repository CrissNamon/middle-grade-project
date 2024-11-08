package ru.danilarassokhin.game.sql.repository;

import java.util.Optional;

import ru.danilarassokhin.game.sql.service.TransactionContext;

public interface JdbcRepository<E, I> {

  I save(TransactionContext ctx, E entity);

  Optional<E> findById(TransactionContext ctx, I id);

  boolean existsById(TransactionContext ctx, I id);

}
