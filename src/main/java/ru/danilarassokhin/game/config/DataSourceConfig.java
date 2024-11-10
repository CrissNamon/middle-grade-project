package ru.danilarassokhin.game.config;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariDataSource;
import net.ttddyy.dsproxy.listener.logging.DefaultQueryLogEntryCreator;
import net.ttddyy.dsproxy.listener.logging.SLF4JQueryLoggingListener;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.flywaydb.core.Flyway;
import ru.danilarassokhin.game.util.PropertiesFactory;
import tech.hiddenproject.progressive.annotation.ComponentScan;
import tech.hiddenproject.progressive.annotation.Configuration;
import tech.hiddenproject.progressive.annotation.GameBean;

@Configuration
@ComponentScan("ru.danilarassokhin.game.sql.service.impl")
public class DataSourceConfig {

  public static final Integer TRANSACTION_DEFAULT_RETRY_COUNT = 10;

  private static final String HIKARI_DATASOURCE_JDBC_URL_PROPERTY = "datasource.jdbc.url";
  private static final String HIKARI_DATASOURCE_USER_NAME_PROPERTY = "datasource.jdbc.user";
  private static final String HIKARI_DATASOURCE_PASSWORD_PROPERTY = "datasource.jdbc.password";
  private static final String HIKARI_DATASOURCE_MINIMUM_IDLE_PROPERTY = "datasource.pool.minimum-idle";
  private static final String HIKARI_DATASOURCE_MAXIMUM_POOL_SIZE_PROPERTY = "datasource.pool.maximum-size";

  @GameBean
  public DataSource hikariDataSource(PropertiesFactory propertiesFactory) {
    SLF4JQueryLoggingListener loggingListener = new SLF4JQueryLoggingListener();
    loggingListener.setQueryLogEntryCreator(new DefaultQueryLogEntryCreator());
    var dataSource = new HikariDataSource();
    dataSource.setMinimumIdle(propertiesFactory.getAsInt(HIKARI_DATASOURCE_MINIMUM_IDLE_PROPERTY).orElseThrow());
    dataSource.setMaximumPoolSize(propertiesFactory.getAsInt(HIKARI_DATASOURCE_MAXIMUM_POOL_SIZE_PROPERTY).orElseThrow());
    dataSource.setJdbcUrl(propertiesFactory.getAsString(HIKARI_DATASOURCE_JDBC_URL_PROPERTY).orElseThrow());
    dataSource.setUsername(propertiesFactory.getAsString(HIKARI_DATASOURCE_USER_NAME_PROPERTY).orElseThrow());
    dataSource.setPassword(propertiesFactory.getAsString(HIKARI_DATASOURCE_PASSWORD_PROPERTY).orElseThrow());
    dataSource.setDataSourceProperties(propertiesFactory.getAll());
    return ProxyDataSourceBuilder.create(dataSource)
        .listener(loggingListener)
        .logQueryByLog4j()
        .writeIsolation()
        .build();
  }

  @GameBean
  public Flyway flyway(DataSource dataSource, PropertiesFactory propertiesFactory) {
    var flyway = Flyway.configure()
        .dataSource(dataSource)
        .configuration(propertiesFactory.getAll())
        .load();
    flyway.migrate();
    return flyway;
  }

}
