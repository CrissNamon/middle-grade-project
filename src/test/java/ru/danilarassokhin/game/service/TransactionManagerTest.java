package ru.danilarassokhin.game.service;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.danilarassokhin.game.sql.service.impl.TransactionManagerImpl;

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
    var query = "SELECT * FROM test_table WHERE column1 = ? AND column2 = ?;";
    var values = new Object[] {"value1", 1};
    var expectedResult = "result";
    var statement = Mockito.mock(PreparedStatement.class);
    var resultSet = Mockito.mock(ResultSet.class);
    var sqlCaptor = ArgumentCaptor.forClass(String.class);

    Mockito.when(dataSource.getConnection()).thenReturn(connection);
    Mockito.when(connection.prepareStatement(Mockito.any())).thenReturn(statement);
    Mockito.when(statement.executeQuery()).thenReturn(resultSet);

    var actual = transactionManager.executeQuery(query, rs -> expectedResult, values);
    Mockito.verify(connection, Mockito.times(1)).prepareStatement(sqlCaptor.capture());
    Mockito.verify(statement, Mockito.times(1)).setString(1, (String) values[0]);
    Mockito.verify(statement, Mockito.times(1)).setInt(2, (Integer) values[1]);
    Assertions.assertEquals(query, sqlCaptor.getValue());
    Assertions.assertEquals(expectedResult, actual);
  }
  @Test
  public void itShouldCreateValidUpdateQuery() throws SQLException {
    var query = "UPDATE test_table SET column1 = ? WHERE column2 = ?;";
    var values = new Object[] {"value1", 1};
    var expectedResult = 1;
    var isolationLevel = Connection.TRANSACTION_READ_COMMITTED;
    var statement = Mockito.mock(PreparedStatement.class);
    var sqlCaptor = ArgumentCaptor.forClass(String.class);
    var isolationLevelCaptor = ArgumentCaptor.forClass(Integer.class);

    Mockito.when(dataSource.getConnection()).thenReturn(connection);
    Mockito.when(connection.prepareStatement(Mockito.any())).thenReturn(statement);
    Mockito.when(statement.executeUpdate()).thenReturn(expectedResult);

    var actual = transactionManager.executeUpdate(isolationLevel, query, values);
    Mockito.verify(connection, Mockito.times(1)).prepareStatement(sqlCaptor.capture());
    Mockito.verify(statement, Mockito.times(1)).setString(1, (String) values[0]);
    Mockito.verify(statement, Mockito.times(1)).setInt(2, (Integer) values[1]);
    Mockito.verify(connection, Mockito.times(1)).setTransactionIsolation(isolationLevelCaptor.capture());
    Assertions.assertEquals(query, sqlCaptor.getValue());
    Assertions.assertEquals(expectedResult, actual);
    Assertions.assertEquals(isolationLevel, isolationLevelCaptor.getValue());
  }

}
