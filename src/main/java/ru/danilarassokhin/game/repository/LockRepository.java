package ru.danilarassokhin.game.repository;

import ru.danilarassokhin.game.sql.service.TransactionContext;

/**
 * Repository to work with locks.
 */
public interface LockRepository {

  /**
   * Acquires lock with name or waits given time.
   * @param ctx {@link TransactionContext}
   * @param name Lock name
   * @param waitTime Wait time to acquire lock
   */
  void acquireLockOrWait(TransactionContext ctx, String name, Integer waitTime);

}
