package ru.danilarassokhin.game.sql.service.impl;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

import ru.danilarassokhin.game.exception.DataIntegrityException;
import ru.danilarassokhin.game.exception.DataSourceException;
import ru.danilarassokhin.game.sql.service.TransactionManager;
import ru.danilarassokhin.game.sql.service.TransactionTemplate;
import ru.danilarassokhin.game.util.PropertiesFactory;
import ru.danilarassokhin.game.util.SneakyConsumer;
import ru.danilarassokhin.game.util.SneakyFunction;
import tech.hiddenproject.aide.optional.ThrowableOptional;
import tech.hiddenproject.progressive.annotation.Autofill;
import tech.hiddenproject.progressive.annotation.GameBean;

@GameBean
public class TransactionManagerImpl implements TransactionManager {

  private final DataSource dataSource;
  private final String defaultSchemaName;

  @Autofill
  public TransactionManagerImpl(DataSource dataSource, PropertiesFactory propertiesFactory) {
    this.dataSource = dataSource;
    this.defaultSchemaName = propertiesFactory.getAsString("datasource.defaultSchema").orElse(null);
  }

  @Override
  public <T> T startTransaction(SneakyFunction<Connection, T> body) {
    Connection connection = null;
    try {
      connection = dataSource.getConnection();
      connection.setAutoCommit(false);
      var result = body.apply(connection);
      connection.commit();
      return result;
    } catch (DataSourceException | DataIntegrityException e) {
      rollbackSafely(connection);
      throw e;
    } catch (Throwable e) {
      throw new DataSourceException(e);
    } finally {
      closeSafely(connection);
    }
  }

  @Override
  public int executeUpdate(Connection connection, String query, Object... args) {
    return ThrowableOptional.sneaky(() -> {
      var statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
      fillStatement(statement, Arrays.stream(args).toList());
      return statement.executeUpdate();
    });
  }

  @Override
  public <T> T executeQuery(Connection connection, String query, SneakyFunction<ResultSet, T> processor, Object... args) {
    return ThrowableOptional.sneaky(() -> {
      var statement = connection.prepareStatement(query);
      fillStatement(statement, Arrays.stream(args).toList());
      return processor.apply(statement.executeQuery());
    });
  }

  @Override
  public void doInTransaction(SneakyConsumer<TransactionTemplate> body) {
    fetchInTransaction(transactionTemplate -> {
      body.accept(transactionTemplate);
      return null;
    });
  }

  @Override
  public void doInTransaction(int isolationLevel, SneakyConsumer<TransactionTemplate> body) {
    fetchInTransaction(isolationLevel, transactionTemplate -> {
      body.accept(transactionTemplate);
      return null;
    });
  }

  @Override
  public <T> T fetchInTransaction(SneakyFunction<TransactionTemplate, T> body) {
    return fetchInTransaction(Connection.TRANSACTION_READ_COMMITTED, body);
  }

  @Override
  public <T> T fetchInTransaction(int isolationLevel, SneakyFunction<TransactionTemplate, T> body) {
    return startTransaction(connection -> {
      connection.setTransactionIsolation(isolationLevel);
      var transactionTemplate = new TransactionTemplateImpl(connection, new DefaultRepository(this));
      transactionTemplate.useSchema(defaultSchemaName);
      return body.apply(transactionTemplate);
    });
  }

  private void fillStatement(PreparedStatement statement, Collection<?> values) {
    var index = 1;
    try {
      for (var iterator = values.iterator(); iterator.hasNext(); index++) {
        var value = iterator.next();
        switch (value) {
          case String s -> statement.setString(index, s);
          case Integer i -> statement.setInt(index, i);
          case Long l -> statement.setLong(index, l);
          case Boolean b -> statement.setBoolean(index, b);
          case Double d -> statement.setDouble(index, d);
          case UUID uuid -> statement.setObject(index, uuid);
          case Enum<?> e -> statement.setString(index, e.name());
          default -> statement.setObject(index, value);
        }
      }
    } catch (SQLIntegrityConstraintViolationException e) {
      throw new DataIntegrityException(e);
    } catch (SQLException e) {
      throw new DataSourceException(e);
    }
  }

  private void rollbackSafely(Connection connection) {
    if (connection != null) {
      ThrowableOptional.sneaky(() -> connection.rollback());
    }
  }

  private void closeSafely(Connection connection) {
    if (connection != null) {
      ThrowableOptional.sneaky(connection::close);
    }
  }
}
