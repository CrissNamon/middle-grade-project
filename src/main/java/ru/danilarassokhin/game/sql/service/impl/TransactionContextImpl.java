package ru.danilarassokhin.game.sql.service.impl;

import java.sql.Connection;
import java.util.Objects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.danilarassokhin.game.sql.service.QueryContext;
import ru.danilarassokhin.game.sql.service.TransactionContext;
import ru.danilarassokhin.game.util.SneakyFunction;
import tech.hiddenproject.aide.optional.ThrowableOptional;

@RequiredArgsConstructor
public class TransactionContextImpl implements TransactionContext {

  @Getter
  private final Connection connection;
  private final DefaultRepository defaultRepository;
  private String schemaName;

  @Override
  public QueryContext query(String query, Object... args) {
    return new QueryContextImpl(connection, defaultRepository, query, args);
  }

  @Override
  public void useSchema(String schemaName) {
    this.schemaName = schemaName;
    setSchema();
  }

  @Override
  public void readOnly() {
    ThrowableOptional.sneaky(() -> connection.setReadOnly(true));
  }

  @Override
  public <T> T rawQuery(SneakyFunction<Connection, T> mapper) {
    return ThrowableOptional.sneaky(() -> mapper.apply(connection));
  }

  private void setSchema() {
    if (Objects.nonNull(schemaName)) {
      ThrowableOptional.sneaky(() -> connection.setSchema(schemaName));
    }
  }
}
