package ru.danilarassokhin.game.config;

import javax.sql.DataSource;

import java.util.Properties;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.utils.ConnectionProvider;
import org.quartz.utils.DBConnectionManager;
import ru.danilarassokhin.game.exception.QuartzException;
import ru.danilarassokhin.game.repository.MailRepository;
import ru.danilarassokhin.game.worker.kafka.KafkaMailSender;
import ru.danilarassokhin.game.worker.quartz.DataSourceConnectionProvider;
import ru.danilarassokhin.game.worker.quartz.DiJobFactory;
import ru.danilarassokhin.game.worker.quartz.QuartzJobContainer;
import ru.danilarassokhin.game.worker.quartz.jobs.MailSenderJob;
import ru.danilarassokhin.sql.service.TransactionManager;
import ru.danilarassokhin.util.PropertiesFactory;
import tech.hiddenproject.progressive.BasicComponentManager;
import tech.hiddenproject.progressive.annotation.Configuration;
import tech.hiddenproject.progressive.annotation.GameBean;

@Configuration
public class QuartzConfig {

  public static final String QUARTZ_DATA_SOURCE_NAME = "gameDataSource";

  private static final String QUARTZ_PROPERTY_PREFIX = "quartz";
  private static final String QUARTZ_PROPERTY_NAME_FORMAT = "org.quartz.%s";
  private static final String QUARTZ_JOB_STORE_DATA_SOURCE_PROPERTY = "org.quartz.jobStore.dataSource";

  @GameBean(order = 0)
  public ConnectionProvider quartzConnectionProvider(DataSource dataSource) {
    var connectionProvider = new DataSourceConnectionProvider(dataSource);
    DBConnectionManager.getInstance().addConnectionProvider(QUARTZ_DATA_SOURCE_NAME, connectionProvider);
    return connectionProvider;
  }

  @GameBean(order = 1)
  public Scheduler scheduler(PropertiesFactory propertiesFactory, ConnectionProvider quartzConnectionProvider) {
    try {
      var scheduler = new StdSchedulerFactory(schedulerProperties(propertiesFactory)).getScheduler();
      scheduler.setJobFactory(new DiJobFactory(BasicComponentManager.getDiContainer()));
      return scheduler;
    } catch (SchedulerException e) {
      throw new QuartzException("Error while bootstrapping scheduler", e);
    }
  }

  @GameBean(order = 2)
  public MailSenderJob mailSenderJob(
      TransactionManager transactionManager,
      MailRepository mailRepository,
      KafkaMailSender kafkaMailSender,
      PropertiesFactory propertiesFactory
  ) {
    return new MailSenderJob(transactionManager, mailRepository, kafkaMailSender, propertiesFactory);
  }

  @GameBean(order = 3)
  public QuartzJobContainer quartzJobContainer(Scheduler scheduler, MailSenderJob mailSenderJob) {
    return new QuartzJobContainer(scheduler, mailSenderJob);
  }

  private Properties schedulerProperties(PropertiesFactory propertiesFactory) {
    var properties = new Properties();
    propertiesFactory.getAllForPrefix(QUARTZ_PROPERTY_PREFIX).stringPropertyNames()
        .forEach(name -> propertiesFactory.getAsString(QUARTZ_PROPERTY_PREFIX + "." + name)
            .ifPresent(value -> properties.setProperty(QUARTZ_PROPERTY_NAME_FORMAT.formatted(name), value)));
    properties.setProperty(QUARTZ_JOB_STORE_DATA_SOURCE_PROPERTY, QUARTZ_DATA_SOURCE_NAME);
    return properties;
  }

}
