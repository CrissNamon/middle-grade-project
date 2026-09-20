package ru.danilarassokhin.statistic.config;


import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.clickhouse.ClickHouseContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.danilarassokhin.statistic.annotation.EmbeddedKafkaTest;

@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
@EmbeddedKafkaTest
public abstract class IntegrationTest {

  @Container
  @ServiceConnection
  static final ClickHouseContainer clickHouseContainer =
    new ClickHouseContainer("clickhouse/clickhouse-server:21.11-alpine");

}
