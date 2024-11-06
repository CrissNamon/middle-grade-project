package ru.danilarassokhin.game.sql.service.impl;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.danilarassokhin.game.sql.service.TransactionTemplate;
import ru.danilarassokhin.game.util.TypeUtils;
import tech.hiddenproject.aide.optional.ThrowableOptional;

@RequiredArgsConstructor
public class TransactionTemplateImpl implements TransactionTemplate {

  @Getter
  private final Connection connection;
  private final DefaultRepository defaultRepository;
  private String schemaName;

  @Override
  public <T> List<Map<String, T>> fetchRaw(String query, Object... args) {
    setSchema();
    return TypeUtils.cast(defaultRepository.executeQuery(connection, null, query, true, args));
  }

  @Override
  public <T> Map<String, T> fetchOneRaw(String query, Object... args) {
    setSchema();
    return TypeUtils.cast(fetchRaw(query, args).getFirst());
  }

  @Override
  public <T> List<T> fetch(String query, Class<T> type, Object... args) {
    setSchema();
    return TypeUtils.cast(defaultRepository.executeQuery(connection, type, query, false, args));
  }

  @Override
  public <T> T fetchOne(String query, Class<T> type, Object... args) {
    setSchema();
    return TypeUtils.cast(defaultRepository.executeQuery(connection, type, query, false, args).stream().findFirst().orElse(null));
  }

  @Override
  public int executeUpdate(String query, Object... args) {
    setSchema();
    return defaultRepository.executeUpdate(connection, query, args);
  }

  @Override
  public void useSchema(String schemaName) {
    this.schemaName = schemaName;
  }

  private void setSchema() {
    if (Objects.nonNull(schemaName)) {
      ThrowableOptional.sneaky(() -> connection.setSchema(schemaName));
    }
  }
}
