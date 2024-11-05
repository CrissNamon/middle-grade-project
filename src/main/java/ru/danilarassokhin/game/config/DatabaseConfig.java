package ru.danilarassokhin.game.config;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariDataSource;
import ru.danilarassokhin.game.util.PropertiesFactory;
import tech.hiddenproject.progressive.annotation.ComponentScan;
import tech.hiddenproject.progressive.annotation.Configuration;
import tech.hiddenproject.progressive.annotation.GameBean;

@Configuration
@ComponentScan("ru.danilarassokhin.game.repository")
public class DatabaseConfig {

  private static final String HIKARI_DATASOURCE_JDBC_URL_PROPERTY = "datasource.jdbc.url";
  private static final String HIKARI_DATASOURCE_USER_NAME_PROPERTY = "datasource.jdbc.user";
  private static final String HIKARI_DATASOURCE_PASSWORD_PROPERTY = "datasource.jdbc.password";
  private static final String HIKARI_DATASOURCE_MINIMUM_IDLE_PROPERTY = "datasource.pool.minimum-idle";
  private static final String HIKARI_DATASOURCE_MAXIMUM_POOL_SIZE_PROPERTY = "datasource.pool.maximum-size";

  @GameBean
  public DataSource hikariDataSource(PropertiesFactory propertiesFactory) {
    var dataSource = new HikariDataSource();
    dataSource.setMinimumIdle(propertiesFactory.getAsInt(HIKARI_DATASOURCE_MINIMUM_IDLE_PROPERTY).orElseThrow());
    dataSource.setMaximumPoolSize(propertiesFactory.getAsInt(HIKARI_DATASOURCE_MAXIMUM_POOL_SIZE_PROPERTY).orElseThrow());
    dataSource.setJdbcUrl(propertiesFactory.getAsString(HIKARI_DATASOURCE_JDBC_URL_PROPERTY).orElseThrow());
    dataSource.setUsername(propertiesFactory.getAsString(HIKARI_DATASOURCE_USER_NAME_PROPERTY).orElseThrow());
    dataSource.setPassword(propertiesFactory.getAsString(HIKARI_DATASOURCE_PASSWORD_PROPERTY).orElseThrow());
    return dataSource;
  }

}
