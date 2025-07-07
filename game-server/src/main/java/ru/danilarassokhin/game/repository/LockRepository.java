package ru.danilarassokhin.game.repository;

/**
 * Repository to work with locks.
 */
public interface LockRepository {

  /**
   * Acquires lock with name or waits given time.
   * @param name Lock name
   * @param waitTime Wait time to acquire lock
   */
  void acquireLockOrWait(String name, Integer waitTime);

}
