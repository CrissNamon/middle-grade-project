package ru.danilarassokhin.game.config;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariDataSource;
import net.ttddyy.dsproxy.listener.logging.DefaultQueryLogEntryCreator;
import net.ttddyy.dsproxy.listener.logging.SLF4JQueryLoggingListener;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.flywaydb.core.Flyway;
import ru.danilarassokhin.util.PropertiesFactory;
import tech.hiddenproject.progressive.annotation.Configuration;
import tech.hiddenproject.progressive.annotation.GameBean;

@Configuration
public class DataSourceConfig {

  public static final Integer TRANSACTION_DEFAULT_RETRY_COUNT = 10;

  private static final String HIKARI_DATASOURCE_JDBC_URL_PROPERTY = "datasource.jdbc.url";
  private static final String HIKARI_DATASOURCE_USER_NAME_PROPERTY = "datasource.jdbc.user";
  private static final String HIKARI_DATASOURCE_PASSWORD_PROPERTY = "datasource.jdbc.password";
  private static final String HIKARI_DATASOURCE_MINIMUM_IDLE_PROPERTY = "datasource.pool.minimum-idle";
  private static final String HIKARI_DATASOURCE_MAXIMUM_POOL_SIZE_PROPERTY = "datasource.pool.maximum-size";
  private static final String HIKARI_DATASOURCE_LEAK_DETECTION_THRESHOLD_PROPERTY = "datasource.leakDetectionThreshold";
  private static final String FLYWAY_LOCATIONS_PROPERTY = "flyway.locations";

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
    dataSource.setLeakDetectionThreshold(propertiesFactory.getAsInt(HIKARI_DATASOURCE_LEAK_DETECTION_THRESHOLD_PROPERTY).orElseThrow());
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
        .locations(propertiesFactory.getAsString(FLYWAY_LOCATIONS_PROPERTY).orElseThrow())
        .defaultSchema("game")
        .schemas("game")
        .baselineOnMigrate(true)
        .load();
    flyway.migrate();
    System.out.println("MIGRATING");
    return flyway;
  }

}
