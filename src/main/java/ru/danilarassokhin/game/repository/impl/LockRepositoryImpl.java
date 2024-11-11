package ru.danilarassokhin.game.repository.impl;

import ru.danilarassokhin.game.repository.LockRepository;
import ru.danilarassokhin.game.sql.service.TransactionContext;
import tech.hiddenproject.progressive.annotation.GameBean;

/**
 * Repository to work with locks.
 */
@GameBean
public class LockRepositoryImpl implements LockRepository {

  private static final String SET_LOCK_TIMEOUT_QUERY = "SET LOCAL lock_timeout = '%ds';";
  private static final String XACT_LOCK_QUERY = "SELECT 1, pg_advisory_xact_lock(hashtext(?));";

  /**
   * Acquires lock with name or waits given time.
   * @param ctx {@link TransactionContext}
   * @param name Lock name
   * @param waitTime Wait time to acquire lock
   */
  @Override
  public void acquireLockOrWait(TransactionContext ctx, String name, Integer waitTime) {
    var lockTimeOut = String.format(SET_LOCK_TIMEOUT_QUERY, waitTime);
    ctx.query(lockTimeOut).execute();
    ctx.rawQuery(connection -> {
      var lockStatement = connection.prepareStatement(XACT_LOCK_QUERY);
      lockStatement.setString(1, name);
      lockStatement.execute();
      return null;
    });
  }
}
