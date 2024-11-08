package ru.danilarassokhin.game.sql.service.impl;

import java.sql.Connection;
import java.util.Objects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.danilarassokhin.game.exception.DataSourceException;
import ru.danilarassokhin.game.sql.service.JdbcMapperService;
import ru.danilarassokhin.game.sql.service.QueryContext;
import ru.danilarassokhin.game.sql.service.TransactionContext;
import ru.danilarassokhin.game.sql.service.QueryFunction;
import tech.hiddenproject.aide.optional.ThrowableOptional;

@RequiredArgsConstructor
public class TransactionContextImpl implements TransactionContext {

  @Getter
  private final Connection connection;
  private final JdbcMapperService jdbcMapperService;
  private String schemaName;

  @Override
  public QueryContext query(String query, Object... args) {
    return new QueryContextImpl(connection, jdbcMapperService, query, args);
  }

  @Override
  public void useSchema(String schemaName) {
    this.schemaName = schemaName;
    setSchema();
  }

  @Override
  public void readOnly() throws DataSourceException {
    ThrowableOptional.sneaky(() -> connection.setReadOnly(true), DataSourceException::new);
  }

  @Override
  public <T> T rawQuery(QueryFunction<Connection, T> mapper) throws DataSourceException {
    return ThrowableOptional.sneaky(() -> mapper.apply(connection), DataSourceException::new);
  }

  private void setSchema() {
    if (Objects.nonNull(schemaName)) {
      ThrowableOptional.sneaky(() -> connection.setSchema(schemaName), DataSourceException::new);
    }
  }
}
