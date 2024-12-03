package ru.danilarassokhin.game.sql.service.impl;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import ru.danilarassokhin.game.exception.DataSourceException;
import ru.danilarassokhin.game.exception.DataSourceConnectionException;
import ru.danilarassokhin.game.factory.CircuitBreakerFactory;
import ru.danilarassokhin.game.sql.service.JdbcMapperService;
import ru.danilarassokhin.game.sql.service.TransactionManager;
import ru.danilarassokhin.game.sql.service.TransactionContext;
import ru.danilarassokhin.game.util.PropertiesFactory;
import ru.danilarassokhin.game.sql.service.QueryConsumer;
import ru.danilarassokhin.game.sql.service.QueryFunction;
import tech.hiddenproject.aide.optional.ThrowableOptional;
import tech.hiddenproject.progressive.annotation.Autofill;
import tech.hiddenproject.progressive.annotation.GameBean;

@GameBean
public class TransactionManagerImpl implements TransactionManager {

  private static final String DATASOURCE_DEFAULT_SCHEME_PROPERTY = "datasource.defaultSchema";
  /**
   * 08000	connection_exception
   * 08003	connection_does_not_exist
   * 08006	connection_failure
   * 08001	sqlclient_unable_to_establish_sqlconnection
   * 08004	sqlserver_rejected_establishment_of_sqlconnection
   * https://www.postgresql.org/docs/current/errcodes-appendix.html
   */
  private static final Set<String> CONNECTION_EXCEPTION_SQL_STATES = Set.of("08000","08001", "08003", "08004", "08006");

  private final ThreadLocal<Connection> transactionContextThreadLocal = new ThreadLocal<>();

  private final DataSource dataSource;
  private final String defaultSchemaName;
  private final JdbcMapperService jdbcMapperService;
  private final CircuitBreaker circuitBreaker;

  @Autofill
  public TransactionManagerImpl(
      DataSource dataSource,
      PropertiesFactory propertiesFactory,
      JdbcMapperService jdbcMapperServiceImpl,
      CircuitBreakerFactory circuitBreakerFactory
  ) {
    this.dataSource = dataSource;
    this.jdbcMapperService = jdbcMapperServiceImpl;
    this.defaultSchemaName = propertiesFactory.getAsString(DATASOURCE_DEFAULT_SCHEME_PROPERTY).orElse(null);
    this.circuitBreaker = circuitBreakerFactory.create(getClass().getCanonicalName(), DataSourceConnectionException.class);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T startTransaction(QueryFunction<Connection, T> body) {
    return circuitBreaker.executeSupplier(() -> {
      Connection connection = transactionContextThreadLocal.get();
      try {
        openTransaction();
        var result = body.apply(connection);
        connection.commit();
        System.out.println("RESULT: " + result);
        return result;
      } catch (RuntimeException e) {
        rollbackSafely(connection);
        throw e;
      } catch (SQLException e) {
        if (CONNECTION_EXCEPTION_SQL_STATES.contains(e.getSQLState())) {
          throw new DataSourceConnectionException(e);
        }
        throw new DataSourceException(e);
      } finally {
        closeSafely(connection);
      }
    });
  }

  @Override
  public void doInTransaction(QueryConsumer<TransactionContext> body) {
    fetchInTransaction(transactionTemplate -> {
      body.accept(transactionTemplate);
      return null;
    });
  }

  @Override
  public void doInTransaction(int isolationLevel, QueryConsumer<TransactionContext> body) {
    fetchInTransaction(isolationLevel, transactionTemplate -> {
      body.accept(transactionTemplate);
      return null;
    });
  }

  @Override
  public <T> T fetchInTransaction(QueryFunction<TransactionContext, T> body) {
    return fetchInTransaction(Connection.TRANSACTION_READ_COMMITTED, body);
  }

  @Override
  public <T> T fetchInTransaction(int isolationLevel, QueryFunction<TransactionContext, T> body) {
    var connection = transactionContextThreadLocal.get();
    return handleConnectionAction(connection, c -> {
      c.setTransactionIsolation(isolationLevel);
      var transactionTemplate = new TransactionContextImpl(c, jdbcMapperService, defaultSchemaName);
      return body.apply(transactionTemplate);
    });
  }

  @Override
  public void commit() {
    var connection = transactionContextThreadLocal.get();
    if (Objects.nonNull(connection)) {
      ThrowableOptional.sneaky(connection::commit);
      closeSafely(connection);
      System.out.println("COMMITING");
    }
  }

  @Override
  public void openTransaction() {
    circuitBreaker.executeRunnable(() -> {
      Connection connection = transactionContextThreadLocal.get();
      try {
        if (Objects.isNull(connection)) {
          connection = dataSource.getConnection();
          connection.setAutoCommit(false);
          transactionContextThreadLocal.set(connection);
        }
      } catch (RuntimeException e) {
        rollbackSafely(connection);
        throw e;
      } catch (SQLException e) {
        if (CONNECTION_EXCEPTION_SQL_STATES.contains(e.getSQLState())) {
          throw new DataSourceConnectionException(e);
        }
        throw new DataSourceException(e);
      }
    });
  }

  private <T> T handleConnectionAction(Connection connection, QueryFunction<Connection, T> action) {
    try {
      if (Objects.nonNull(connection)) {
        return action.apply(connection);
      }
      throw new SQLException("No active transaction");
    } catch (RuntimeException e) {
      rollbackSafely(connection);
      throw e;
    } catch (SQLException e) {
      if (CONNECTION_EXCEPTION_SQL_STATES.contains(e.getSQLState())) {
        throw new DataSourceConnectionException(e);
      }
      throw new DataSourceException(e);
    }
  }

  private void rollbackSafely(Connection connection) {
    if (connection != null) {
      ThrowableOptional.sneaky(() -> connection.rollback(), DataSourceException::new);
    }
  }

  private void closeSafely(Connection connection) {
    if (connection != null) {
      ThrowableOptional.sneaky(connection::close, DataSourceException::new);
    }
  }
}
