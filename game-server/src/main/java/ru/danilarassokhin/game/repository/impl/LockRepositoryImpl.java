package ru.danilarassokhin.game.repository.impl;

import lombok.RequiredArgsConstructor;
import ru.danilarassokhin.game.repository.LockRepository;
import ru.danilarassokhin.sql.service.TransactionManager;
import tech.hiddenproject.progressive.annotation.Autofill;
import tech.hiddenproject.progressive.annotation.GameBean;

/**
 * Repository to work with locks.
 */
@GameBean
@RequiredArgsConstructor(onConstructor_ = {@Autofill})
public class LockRepositoryImpl implements LockRepository {

  private static final String SET_LOCK_TIMEOUT_QUERY = "SET LOCAL lock_timeout = '%ds';";
  private static final String XACT_LOCK_QUERY = "SELECT pg_advisory_xact_lock(hashtext(?));";

  private final TransactionManager transactionManager;

  /**
   * Acquires lock with name or waits given time.
   * @param name Lock name
   * @param waitTime Wait time to acquire lock
   */
  @Override
  public void acquireLockOrWait(String name, Integer waitTime) {
    transactionManager.doInTransaction(ctx -> {
      var lockTimeOut = String.format(SET_LOCK_TIMEOUT_QUERY, waitTime);
      ctx.query(lockTimeOut).execute();
      ctx.rawQuery(connection -> {
        try(var lockStatement = connection.prepareStatement(XACT_LOCK_QUERY)) {
          lockStatement.setString(1, name);
          lockStatement.execute();
        }
        return null;
      });
    });
  }
}
