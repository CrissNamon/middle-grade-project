package ru.danilarassokhin.game.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import ru.danilarassokhin.game.mapper.MailMapper;
import ru.danilarassokhin.game.service.ClientAuthenticationService;
import ru.danilarassokhin.game.service.impl.ClientAuthenticationServiceImpl;
import ru.danilarassokhin.messaging.dto.CreateMailDto;
import ru.danilarassokhin.game.repository.MailRepository;
import ru.danilarassokhin.game.worker.kafka.KafkaMailSender;
import ru.danilarassokhin.game.worker.kafka.KafkaMailSenderImpl;
import ru.danilarassokhin.util.PropertiesFactory;
import tech.hiddenproject.progressive.BasicComponentManager;
import tech.hiddenproject.progressive.annotation.Configuration;
import tech.hiddenproject.progressive.annotation.GameBean;

@Configuration
public class KafkaConfig {

  private static final String KAFKA_PROPERTY_PREFIX = "kafka";

  @GameBean(order = 1)
  public Producer<String, CreateMailDto> producer(PropertiesFactory propertiesFactory) {
    return new KafkaProducer<>(propertiesFactory.getAllForPrefix(KAFKA_PROPERTY_PREFIX));
  }

  @GameBean(order = 3)
  public ClientAuthenticationService clientAuthenticationService(
      PropertiesFactory propertiesFactory,
      ObjectMapper objectMapper
  ) {
    return new ClientAuthenticationServiceImpl(propertiesFactory, objectMapper);
  }

  @GameBean(order = 4)
  public KafkaMailSender kafkaMailSender(
      MailRepository mailRepository,
      KafkaProducer<String, CreateMailDto> producer,
      MailMapper mapper,
      PropertiesFactory propertiesFactory,
      ClientAuthenticationService clientAuthenticationService
  ) {
    return BasicComponentManager.getComponentCreator()
        .create(KafkaMailSenderImpl.class, mailRepository, producer, mapper, propertiesFactory, clientAuthenticationService);
  }

}
