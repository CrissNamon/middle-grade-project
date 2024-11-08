package ru.danilarassokhin.game.sql.service.impl;

import java.sql.Connection;
import java.util.Objects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
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
  @SneakyThrows
  public void useSchema(String schemaName) {
    this.schemaName = schemaName;
    setSchema();
  }

  @Override
  @SneakyThrows
  public void readOnly() {
    connection.setReadOnly(true);
  }

  @Override
  @SneakyThrows
  public <T> T rawQuery(QueryFunction<Connection, T> mapper) {
    return mapper.apply(connection);
  }

  private void setSchema() {
    if (Objects.nonNull(schemaName)) {
      ThrowableOptional.sneaky(() -> connection.setSchema(schemaName));
    }
  }
}
