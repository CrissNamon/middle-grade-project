package ru.danilarassokhin.sql.service.impl;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import ru.danilarassokhin.sql.exception.DataSourceException;
import ru.danilarassokhin.sql.service.JdbcMapperService;
import ru.danilarassokhin.sql.service.QueryContext;
import ru.danilarassokhin.util.impl.TypeUtils;
import tech.hiddenproject.aide.optional.ThrowableOptional;

@RequiredArgsConstructor
public class QueryContextImpl implements QueryContext {

  private final Connection connection;
  private final JdbcMapperService jdbcMapperService;
  private final String query;
  private final Object[] args;

  @Override
  public void execute() {
    ThrowableOptional.sneaky(() -> jdbcMapperService.executeUpdate(
        connection, query, args), DataSourceException::new);
  }

  @Override
  public <T> List<T> fetchInto(Class<T> entityType) {
    return ThrowableOptional.sneaky(() -> TypeUtils.cast(jdbcMapperService.executeQuery(
        connection, entityType, query, false, args)), DataSourceException::new);
  }

  @Override
  public <T> T fetchOne(Class<T> entityType) {
    var result = ThrowableOptional.sneaky(() -> jdbcMapperService.executeQuery(
        connection, entityType, query, false, args), DataSourceException::new);
    if (result.size() > 1) {
      throw new DataSourceException("Query returned more than one result");
    }
    return TypeUtils.cast(result.stream().findFirst().orElse(null));
  }

  @Override
  public <T> List<Map<String, T>> fetchRaw() {
    return ThrowableOptional.sneaky(() -> TypeUtils.cast(
        jdbcMapperService.executeQuery(connection, null, query, true, args)), DataSourceException::new);
  }

  @Override
  public <T> Map<String, T> fetchOneRaw() {
    var result = ThrowableOptional.sneaky(() -> jdbcMapperService.executeQuery(
        connection, null, query, true, args), DataSourceException::new);
    if (result.size() > 1) {
      throw new DataSourceException("Query returned more than one result");
    }
    return TypeUtils.cast(result.stream().findFirst().orElse(null));
  }

}
