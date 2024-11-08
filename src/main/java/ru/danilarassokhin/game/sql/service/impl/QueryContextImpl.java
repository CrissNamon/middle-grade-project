package ru.danilarassokhin.game.sql.service.impl;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import ru.danilarassokhin.game.exception.DataSourceException;
import ru.danilarassokhin.game.sql.service.QueryContext;
import ru.danilarassokhin.game.util.TypeUtils;

@RequiredArgsConstructor
public class QueryContextImpl implements QueryContext {

  private final Connection connection;
  private final DefaultRepository defaultRepository;
  private final String query;
  private final Object[] args;

  @Override
  public void execute() {
    defaultRepository.executeUpdate(connection, query, args);
  }

  @Override
  public <T> List<T> fetchInto(Class<T> entityType) {
    return TypeUtils.cast(defaultRepository.executeQuery(connection, entityType, query, false, args));
  }

  @Override
  public <T> T fetchOne(Class<T> entityType) {
    var result = defaultRepository.executeQuery(connection, entityType, query, false, args);
    if (result.size() > 1) {
      throw new DataSourceException("Query returned more than one result");
    }
    return TypeUtils.cast(result.stream().findFirst().orElse(null));
  }

  @Override
  public <T> List<Map<String, T>> fetchRaw() {
    return TypeUtils.cast(defaultRepository.executeQuery(connection, null, query, true, args));
  }

  @Override
  public <T> Map<String, T> fetchOneRaw() {
    var result = defaultRepository.executeQuery(connection, null, query, true, args);
    if (result.size() > 1) {
      throw new DataSourceException("Query returned more than one result");
    }
    return TypeUtils.cast(result.stream().findFirst().orElse(null));
  }

}
