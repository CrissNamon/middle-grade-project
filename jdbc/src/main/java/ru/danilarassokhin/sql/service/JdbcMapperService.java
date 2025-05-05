package ru.danilarassokhin.sql.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface JdbcMapperService {

  List<Object> executeQuery(Connection connection, Class<?> resultType, String query,
                            boolean rawResult, Object... args) throws SQLException;

  int executeUpdate(Connection connection, String query, Object... args) throws SQLException;
}
