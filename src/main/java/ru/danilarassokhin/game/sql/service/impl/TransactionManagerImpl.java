package ru.danilarassokhin.game.sql.service.impl;

import static ru.danilarassokhin.game.config.ResilienceConfig.DATA_SOURCE_CIRCUIT_BREAKER_NAME;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import lombok.extern.slf4j.Slf4j;
import ru.danilarassokhin.game.exception.DataSourceException;
import ru.danilarassokhin.game.exception.DataSourceConnectionException;
import ru.danilarassokhin.game.resilience.annotation.CircuitBreaker;
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
@Slf4j
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

  private final ThreadLocal<Connection> connectionThreadLocal = new ThreadLocal<>();

  private final DataSource dataSource;
  private final String defaultSchemaName;
  private final JdbcMapperService jdbcMapperService;

  @Autofill
  public TransactionManagerImpl(
      DataSource dataSource,
      PropertiesFactory propertiesFactory,
      JdbcMapperService jdbcMapperServiceImpl
  ) {
    this.dataSource = dataSource;
    this.jdbcMapperService = jdbcMapperServiceImpl;
    this.defaultSchemaName = propertiesFactory.getAsString(DATASOURCE_DEFAULT_SCHEME_PROPERTY).orElse(null);
  }

  @Override
  @CircuitBreaker(DATA_SOURCE_CIRCUIT_BREAKER_NAME)
  public <T> T startTransaction(QueryFunction<Connection, T> body) {
    try {
      return startTransaction(dataSource.getConnection(), body);
    } catch (SQLException e) {
      if (CONNECTION_EXCEPTION_SQL_STATES.contains(e.getSQLState())) {
        throw new DataSourceConnectionException(e);
      }
      throw new DataSourceException(e);
    }
  }

  @Override
  @CircuitBreaker(DATA_SOURCE_CIRCUIT_BREAKER_NAME)
  public void doInTransaction(QueryConsumer<TransactionContext> body) {
    fetchInTransaction(transactionTemplate -> {
      body.accept(transactionTemplate);
      return null;
    });
  }

  @Override
  @CircuitBreaker(DATA_SOURCE_CIRCUIT_BREAKER_NAME)
  public void doInTransaction(int isolationLevel, QueryConsumer<TransactionContext> body) {
    fetchInTransaction(isolationLevel, transactionTemplate -> {
      body.accept(transactionTemplate);
      return null;
    });
  }

  @Override
  @CircuitBreaker(DATA_SOURCE_CIRCUIT_BREAKER_NAME)
  public <T> T fetchInTransaction(QueryFunction<TransactionContext, T> body) {
    return fetchInTransaction(Connection.TRANSACTION_READ_COMMITTED, body);
  }

  @Override
  @CircuitBreaker(DATA_SOURCE_CIRCUIT_BREAKER_NAME)
  public <T> T fetchInTransaction(int isolationLevel, QueryFunction<TransactionContext, T> body) {
    QueryFunction<Connection, T> trxBody = (c) -> {
      var transactionTemplate = new TransactionContextImpl(c, jdbcMapperService, defaultSchemaName);
      return body.apply(transactionTemplate);
    };
    var connection = connectionThreadLocal.get();
    if (Objects.isNull(connection)) {
      return startTransaction(c -> {
        c.setTransactionIsolation(isolationLevel);
        return trxBody.apply(c);
      });
    } else {
      return handleConnectionAction(connection, trxBody);
    }
  }

  @Override
  public <T> T executeInTransaction(int isolationLevel, Supplier<T> action) {
    Connection connection = null;
    try {
      connection = dataSource.getConnection();
      log.info("Created new connection: {}", connection.hashCode());
      connection.setAutoCommit(false);
      connection.setTransactionIsolation(isolationLevel);
      connectionThreadLocal.set(connection);
      var result = action.get();
      connection.commit();
      log.info("Committed: {}", connection.hashCode());
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
      connectionThreadLocal.remove();
    }
  }

  private <T> T startTransaction(Connection connection, QueryFunction<Connection, T> body) {
    try {
      var result = body.apply(connection);
      connection.commit();
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
  }

  private <T> T handleConnectionAction(Connection connection, QueryFunction<Connection, T> action) {
    try {
      return action.apply(connection);
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
