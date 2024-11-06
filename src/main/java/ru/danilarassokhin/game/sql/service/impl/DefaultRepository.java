package ru.danilarassokhin.game.sql.service.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.ImmutablePair;
import ru.danilarassokhin.game.exception.DataSourceException;
import ru.danilarassokhin.game.exception.RepositoryException;
import ru.danilarassokhin.game.sql.annotation.Column;
import ru.danilarassokhin.game.sql.annotation.Entity;
import ru.danilarassokhin.game.sql.service.TransactionManager;
import ru.danilarassokhin.game.util.TypeUtils;
import tech.hiddenproject.aide.optional.ThrowableOptional;
import tech.hiddenproject.progressive.BasicComponentManager;
import tech.hiddenproject.progressive.annotation.Autofill;
import tech.hiddenproject.progressive.annotation.GameBean;
import tech.hiddenproject.progressive.injection.GameBeanCreationPolicy;

/**
 * Default proxy for repository.
 */
@GameBean(policy = GameBeanCreationPolicy.OBJECT)
@RequiredArgsConstructor(onConstructor_ = {@Autofill})
public class DefaultRepository {

  private final TransactionManager transactionManager;

  /**
   * Executes SQL query.
   * @param connection {@link Connection} or null get new connection from {@link TransactionManager}
   * @param resultType Return type of intercepted method
   * @param query SQL query
   * @param args SQL query arguments
   * @param rawResult If true returns result as list of Map
   * @return List of results
   */
  public List<Object> executeQuery(Connection connection, Class<?> resultType, String query, boolean rawResult, Object... args) {
    if (connection == null) {
      return transactionManager.startTransaction(c -> executeQuery(c, resultType, query, rawResult, args));
    }
    return transactionManager.executeQuery(connection, processQueryString(query), resultSet -> {
      try(resultSet) {
        if(rawResult) {
          return processRawResult(resultSet);
        }
        if (resultType.isAnnotationPresent(Entity.class)) {
          return processEntityType(resultType, resultSet);
        }
        if (TypeUtils.isPrimitiveOrWrapper(resultType)) {
          return processPrimitiveType(resultSet);
        }
        throw new RepositoryException("Could not process query: " + query);
      } catch (SQLException e) {
        throw new DataSourceException(e);
      }
    }, args);
  }

  /**
   * Executes SQL update query.
   * @param connection {@link Connection} or null get new connection from {@link TransactionManager}
   * @param query SQL query
   * @param args SQL query arguments
   * @return either (1) the row count for SQL Data Manipulation Language (DML) statements or (2) 0
   * for SQL statements that return nothing
   */
  public int executeUpdate(Connection connection, String query, Object... args) {
    if (connection == null) {
      return transactionManager.startTransaction(c -> executeUpdate(c, query, args));
    }
    return transactionManager.executeUpdate(connection, processQueryString(query), args);
  }

  private String processQueryString(String query) {
    return query.trim().replaceAll(System.lineSeparator(), "");
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private Object getParameterValue(ResultSet resultSet, ImmutablePair<String, Class<?>> parameterData) {
    try {
      if (parameterData.getLeft() == null) {
        return null;
      }
      if (parameterData.getRight().equals(UUID.class)) {
        return UUID.fromString(resultSet.getString(parameterData.getLeft()));
      }
      if (parameterData.getRight().isEnum()) {
        var value = resultSet.getString(parameterData.getLeft());
        var enumClass = (Class<? extends Enum>) parameterData.getRight();
        return Enum.valueOf(enumClass, value);
      }
      return resultSet.getObject(parameterData.getLeft());
    } catch (SQLException e) {
      throw new DataSourceException(e);
    }
  }

  private List<Object> processRawResult(ResultSet resultSet) throws SQLException {
    var result = new ArrayList<>();
    while (resultSet.next()) {
      var row = IntStream.range(1, resultSet.getMetaData().getColumnCount() + 1)
          .mapToObj(index -> {
            var key = ThrowableOptional.sneaky(() -> resultSet.getMetaData().getColumnName(index));
            var value = ThrowableOptional.sneaky(() -> resultSet.getObject(index));
            return ImmutablePair.of(key, value);
          }).collect(HashMap::new, (acc, pair) -> acc.put(pair.getLeft(), pair.getRight()), HashMap::putAll);
      result.add(row);
    }
    return result;
  }

  private List<Object> processEntityType(Class<?> resultType, ResultSet resultSet) throws SQLException {
    var resultTypeConstructor = Arrays.stream(resultType.getDeclaredConstructors())
        .filter(this::isConstructorAnnotatedForDb)
        .findFirst()
        .orElseThrow();
    var result = new ArrayList<>();
    while (resultSet.next()) {
      var constructorValues = Arrays.stream(resultTypeConstructor.getParameters())
          .map(this::getParameterData)
          .map(parameterData -> getParameterValue(resultSet, parameterData))
          .filter(Objects::nonNull)
          .toArray();
      result.add(BasicComponentManager.getComponentCreator().create(resultType, constructorValues));
    }
    return result;
  }

  private List<Object> processPrimitiveType(ResultSet resultSet) throws SQLException {
    while (resultSet.next()) {
      return List.of(resultSet.getObject(1));
    }
    return null;
  }

  private ImmutablePair<String, Class<?>> getParameterData(Parameter parameter) {
    var columnName = Optional.ofNullable(parameter.getAnnotation(Column.class))
        .map(Column::value)
        .orElse(null);
    return ImmutablePair.of(columnName, parameter.getType());
  }

  private boolean isConstructorAnnotatedForDb(Constructor<?> constructor) {
    return Arrays.stream(constructor.getParameters())
        .anyMatch(parameter -> parameter.isAnnotationPresent(Column.class));
  }

}
