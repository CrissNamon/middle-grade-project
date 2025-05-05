package ru.danilarassokhin.sql.repository;

import java.util.Optional;

public interface JdbcRepository<E, I> {

  I save(E entity);

  Optional<E> findById(I id);

}
