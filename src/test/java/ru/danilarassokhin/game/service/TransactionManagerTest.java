package ru.danilarassokhin.game.service;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.danilarassokhin.game.service.impl.TransactionManagerImpl;

@ExtendWith(MockitoExtension.class)
public class TransactionManagerTest {

  @Mock
  private Connection connection;

  @Mock
  private DataSource dataSource;

  @InjectMocks
  private TransactionManagerImpl transactionManager;

  @Test
  public void itShouldCreateValidInsertQuery() throws SQLException {
    var table = "test_table";
    var values = Map.of("column1", "value1", "column2", 2);
    var statement = Mockito.mock(PreparedStatement.class);
    var resultSet = Mockito.mock(ResultSet.class);
    var sqlCaptor = ArgumentCaptor.forClass(String.class);
    Mockito.when(dataSource.getConnection()).thenReturn(connection);
    Mockito.when(connection.prepareStatement(Mockito.any())).thenReturn(statement);
    Mockito.when(statement.executeQuery()).thenReturn(resultSet);

    transactionManager.insertQuery(table, values);
    Mockito.verify(connection, Mockito.times(1)).prepareStatement(sqlCaptor.capture());
    Assertions.assertEquals("INSERT INTO " + table + "(column1,column2) VALUES(?,?);", sqlCaptor.getValue());
    Mockito.verify(statement, Mockito.times(1)).setString(0, (String) values.get("column1"));
    Mockito.verify(statement, Mockito.times(1)).setInt(1, (Integer) values.get("column2"));
  }
  @Test
  public void itShouldCreateValidUpdateQuery() throws SQLException {
    var table = "test_table";
    var values = Map.of("column1", "value1", "column2", 2);
    var statement = Mockito.mock(PreparedStatement.class);
    var sqlCaptor = ArgumentCaptor.forClass(String.class);
    Mockito.when(dataSource.getConnection()).thenReturn(connection);
    Mockito.when(connection.prepareStatement(Mockito.any())).thenReturn(statement);
    Mockito.when(statement.executeUpdate()).thenReturn(0);

    transactionManager.updateQuery(Connection.TRANSACTION_REPEATABLE_READ, table, values);
    Mockito.verify(connection, Mockito.times(1)).prepareStatement(sqlCaptor.capture());
    Assertions.assertEquals("UPDATE " + table + " SET column1=?,column2=?;", sqlCaptor.getValue());
    Mockito.verify(statement, Mockito.times(1)).setString(0, (String) values.get("column1"));
    Mockito.verify(statement, Mockito.times(1)).setInt(1, (Integer) values.get("column2"));
  }

}
