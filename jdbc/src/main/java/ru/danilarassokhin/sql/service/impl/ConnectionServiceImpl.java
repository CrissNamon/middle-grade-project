package ru.danilarassokhin.sql.service.impl;

import java.sql.Connection;
import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

import ru.danilarassokhin.sql.service.ConnectionService;
import ru.danilarassokhin.sql.service.QueryFunction;
import tech.hiddenproject.progressive.annotation.GameBean;

@GameBean
public class ConnectionServiceImpl implements ConnectionService {

  @Override
  public int executeUpdate(Connection connection, String query, Object... args) throws SQLException {
    try(var statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
      fillStatement(connection, statement, Arrays.stream(args).toList());
      return statement.executeUpdate();
    }
  }

  @Override
  public <T> T executeQuery(Connection connection, String query, QueryFunction<ResultSet, T> processor, Object... args)
      throws SQLException {
    try(var statement = connection.prepareStatement(query)) {
      fillStatement(connection, statement, Arrays.stream(args).toList());
      return processor.apply(statement.executeQuery());
    }
  }

  private void fillStatement(Connection connection, PreparedStatement statement, Collection<?> values) throws SQLException {
    var index = 1;
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
        case Collection<?> collection -> {
          if (checkCollectionType(collection, Integer.class)) {
            var array = connection.createArrayOf(JDBCType.INTEGER.getName(), collection.toArray());
            statement.setArray(index, array);
          }
        }
        default -> statement.setObject(index, value);
      }
    }
  }

  private boolean checkCollectionType(Collection<?> collection, Class<?> type) {
    return collection.stream().allMatch(Integer.class::isInstance);
  }

}
