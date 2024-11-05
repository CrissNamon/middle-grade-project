package ru.danilarassokhin.game.sql.service.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lombok.Getter;
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

  @Getter
  private Class<?> entityType;

  private final TransactionManager transactionManager;

  /**
   * Executes SQL query.
   * @param resultType Return type of intercepted method
   * @param query SQL query
   * @param args SQL query arguments
   * @return List of results
   */
  public List<Object> executeQuery(Class<?> resultType, String query, boolean rawResult, Object... args) {
    return transactionManager.executeQuery(processQueryString(query), resultSet -> {
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
   * @param query SQL query
   * @param isolation Transaction isolation level
   * @param args SQL query arguments
   * @return either (1) the row count for SQL Data Manipulation Language (DML) statements or (2) 0
   * for SQL statements that return nothing
   */
  public int executeUpdate(String query, int isolation, Object... args) {
    return transactionManager.executeUpdate(isolation, processQueryString(query), args);
  }

  /**
   * Sets original repository entity type.
   * @param entityType Entity type
   */
  public void setEntityType(Class<?> entityType) {
    if (Objects.isNull(this.entityType)) {
      this.entityType = entityType;
      return;
    }
    throw new RepositoryException("Entity type is already set");
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
          }).collect(Collectors.toMap(ImmutablePair::getLeft, ImmutablePair::getRight));
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
