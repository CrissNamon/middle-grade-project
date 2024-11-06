package ru.danilarassokhin.game.sql.service.impl;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.danilarassokhin.game.sql.service.TransactionTemplate;
import ru.danilarassokhin.game.util.TypeUtils;

@RequiredArgsConstructor
public class TransactionTemplateImpl implements TransactionTemplate {

  @Getter
  private final Connection connection;
  private final DefaultRepository defaultRepository;

  @Override
  public <T> List<Map<String, T>> fetchRaw(String query, Object... args) {
    return TypeUtils.cast(defaultRepository.executeQuery(connection, null, query, true, args));
  }

  @Override
  public <T> Map<String, T> fetchOneRaw(String query, Object... args) {
    return TypeUtils.cast(fetchRaw(query, args).getFirst());
  }

  @Override
  public <T> List<T> fetch(String query, Class<T> type, Object... args) {
    return TypeUtils.cast(defaultRepository.executeQuery(connection, type, query, false, args));
  }

  @Override
  public <T> T fetchOne(String query, Class<T> type, Object... args) {
    return TypeUtils.cast(defaultRepository.executeQuery(connection, type, query, false, args).stream().findFirst().orElse(null));
  }

  @Override
  public int executeUpdate(String query, Object... args) {
    return defaultRepository.executeUpdate(connection, query, args);
  }
}
