package ru.danilarassokhin.game.worker.quartz;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;

import lombok.RequiredArgsConstructor;
import org.quartz.utils.ConnectionProvider;
import tech.hiddenproject.progressive.annotation.Autofill;

@RequiredArgsConstructor(onConstructor_ = {@Autofill})
public class DataSourceConnectionProvider implements ConnectionProvider {

  private final DataSource dataSource;

  @Override
  public Connection getConnection() throws SQLException {
    return dataSource.getConnection();
  }

  @Override
  public void initialize() {
    //Data source is initialized by DI container
  }

  @Override
  public void shutdown() {
    //Data source is closed by DI container
  }

}
