package ru.danilarassokhin.game.sql.service.impl;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;

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
          Connection connection = null;
          try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
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
    return startTransaction(connection -> {
      connection.setTransactionIsolation(isolationLevel);
      var transactionTemplate = new TransactionContextImpl(connection, jdbcMapperService);
      transactionTemplate.useSchema(defaultSchemaName);
      return body.apply(transactionTemplate);
    });
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
