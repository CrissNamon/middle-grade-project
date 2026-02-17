package ru.danilarassokhin.statistic.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.context.annotation.Import;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import ru.danilarassokhin.statistic.config.TestKafkaConfig;

@Retention(RetentionPolicy.RUNTIME)
@DirtiesContext
@EmbeddedKafka(partitions = 1, topics = "game.event", kraft = true)
@Import(TestKafkaConfig.class)
public @interface EmbeddedKafkaTest {

}
