package ru.danilarassokhin.notification.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import ru.danilarassokhin.messaging.dto.CreateMailDto;

@Configuration
public class KafkaConfig {

  @Bean("retryableKafkaTemplate")
  public KafkaTemplate<String, CreateMailDto> retryableKafkaTemplate(ProducerFactory<String, CreateMailDto> producerFactory) {
    return new KafkaTemplate<>(producerFactory);
  }

}
