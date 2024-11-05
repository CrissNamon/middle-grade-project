package ru.danilarassokhin.game.service.impl;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import ru.danilarassokhin.game.service.TransactionManager;
import ru.danilarassokhin.game.util.SneakyFunction;
import tech.hiddenproject.aide.optional.ThrowableOptional;
import tech.hiddenproject.progressive.annotation.Autofill;
import tech.hiddenproject.progressive.annotation.GameBean;

@GameBean
@RequiredArgsConstructor(onConstructor_ = {@Autofill})
public class TransactionManagerImpl implements TransactionManager {

  private final DataSource dataSource;

  @Override
  public <T> T startTransaction(SneakyFunction<Connection, T> body) {
    Connection connection = null;
    try {
      connection = dataSource.getConnection();
      var result = body.apply(connection);
      connection.commit();
      return result;
    } catch (RuntimeException e) {
      rollbackSafely(connection);
      throw new RuntimeException(e);
    } catch (Throwable e) {
      throw new RuntimeException(e);
    } finally {
      closeSafely(connection);
    }
  }

  @Override
  public int executeUpdate(int isolationLevel, String query, Object... args) {
    return startTransaction(connection -> {
      connection.setTransactionIsolation(isolationLevel);
      var statement = connection.prepareStatement(query);
      fillStatement(statement, Arrays.stream(args).toList());
      return statement.executeUpdate();
    });
  }

  @Override
  public <T> T executeQuery(String query, SneakyFunction<ResultSet, T> processor, Object... args) {
    return startTransaction(connection -> {
      var statement = connection.prepareStatement(query);
      fillStatement(statement, Arrays.stream(args).toList());
      return processor.apply(statement.executeQuery());
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
          default -> statement.setObject(index, value);
        }
      }
    } catch (Throwable t) {
      throw new RuntimeException(t);
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
