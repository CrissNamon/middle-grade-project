package ru.danilarassokhin.game.config;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.danilarassokhin.game.kafka.KafkaAuthorizationProducerInterceptor;
import ru.danilarassokhin.game.service.ClientAuthenticationService;
import ru.danilarassokhin.game.service.impl.ClientAuthenticationServiceImpl;
import ru.danilarassokhin.messaging.dto.CreateMailDto;
import ru.danilarassokhin.game.worker.kafka.KafkaMailSender;
import ru.danilarassokhin.game.worker.kafka.KafkaMailSenderImpl;
import ru.danilarassokhin.messaging.dto.KafkaHeader;
import ru.danilarassokhin.messaging.kafka.producer.DelegatingKafkaProducer;
import ru.danilarassokhin.messaging.kafka.producer.KafkaProducerInterceptor;
import ru.danilarassokhin.util.PropertiesFactory;
import tech.hiddenproject.progressive.BasicComponentManager;
import tech.hiddenproject.progressive.annotation.Configuration;
import tech.hiddenproject.progressive.annotation.GameBean;

@Configuration
public class KafkaConfig {

  private static final String KAFKA_PROPERTY_PREFIX = "kafka";

  @GameBean(order = 0)
  public ClientAuthenticationService clientAuthenticationService(
      PropertiesFactory propertiesFactory,
      ObjectMapper objectMapper
  ) {
    return new ClientAuthenticationServiceImpl(propertiesFactory, objectMapper);
  }

  @GameBean(order = 1)
  public KafkaProducerInterceptor<String, CreateMailDto> kafkaProducerInterceptor(ClientAuthenticationService clientAuthenticationService) {
    return new KafkaAuthorizationProducerInterceptor(
        KafkaProducerInterceptor.<String, CreateMailDto>filterTopicByRegex(".*").and(
            KafkaProducerInterceptor.<String, CreateMailDto>filterByHeaderPresence(KafkaHeader.AUTHENTICATION).negate()
        ),
        clientAuthenticationService
    );
  }

  @GameBean(order = 2)
  public DelegatingKafkaProducer<String, CreateMailDto> producer(
      PropertiesFactory propertiesFactory,
      KafkaAuthorizationProducerInterceptor kafkaAuthorizationProducerInterceptor
  ) {
    return new DelegatingKafkaProducer<>(propertiesFactory.getAllForPrefix(KAFKA_PROPERTY_PREFIX),
                                         List.of(kafkaAuthorizationProducerInterceptor));
  }

  @GameBean(order = 3)
  public KafkaMailSender kafkaMailSender() {
    return BasicComponentManager.getComponentCreator().create(KafkaMailSenderImpl.class);
  }

}
