package ru.danilarassokhin.statistic.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import ru.danilarassokhin.messaging.dto.event.EventDto;

@TestConfiguration
public class TestKafkaConfig {

  @Bean
  public ProducerFactory<String, EventDto> producerFactory(EmbeddedKafkaBroker embeddedKafkaBroker) {
    Map<String, Object> configProps = new HashMap<>();
    configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, embeddedKafkaBroker.getBrokersAsString());
    var jsonSerializer = new JsonSerializer<EventDto>();
    jsonSerializer.noTypeInfo();
    return new DefaultKafkaProducerFactory<>(
        configProps,
        new StringSerializer(),
        jsonSerializer
    );
  }

  @Bean
  public KafkaTemplate<String, EventDto> kafkaTemplate(EmbeddedKafkaBroker embeddedKafkaBroker) {
    return new KafkaTemplate<>(producerFactory(embeddedKafkaBroker));
  }



}
