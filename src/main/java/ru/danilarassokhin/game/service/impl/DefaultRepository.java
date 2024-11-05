package ru.danilarassokhin.game.service.impl;

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

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.ImmutablePair;
import ru.danilarassokhin.game.service.annotation.Column;
import ru.danilarassokhin.game.service.annotation.Entity;
import ru.danilarassokhin.game.service.TransactionManager;
import ru.danilarassokhin.game.util.TypeUtils;
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
  public List<Object> executeQuery(Class<?> resultType, String query, Object... args) {
    return transactionManager.executeQuery(processQueryString(query), resultSet -> {
      try(resultSet) {
        if (resultType.isAnnotationPresent(Entity.class)) {
          return processEntityType(resultType, resultSet);
        }
        if (TypeUtils.isPrimitiveOrWrapper(resultType)) {
          return processPrimitiveType(resultSet);
        }
        throw new RuntimeException();
      } catch (SQLException e) {
        throw new RuntimeException(e);
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
    throw new RuntimeException();
  }

  private String processQueryString(String query) {
    return query.trim().replaceAll(System.lineSeparator(), "");
  }

  private Object getParameterValue(ResultSet resultSet, ImmutablePair<String, Class<?>> parameterData) {
    try {
      if (parameterData.getLeft() == null) {
        return null;
      }
      if (parameterData.getRight().equals(UUID.class)) {
        return UUID.fromString(resultSet.getString(parameterData.getLeft()));
      }
      return resultSet.getObject(parameterData.getLeft());
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
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
