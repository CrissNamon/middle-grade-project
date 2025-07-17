package ru.danilarassokhin.game.config;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import ru.danilarassokhin.game.mapper.MailMapper;
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

  @GameBean(order = 1)
  public Producer<String, CreateMailDto> producer(PropertiesFactory propertiesFactory) {
    return new KafkaProducer<>(propertiesFactory.getAllForPrefix("kafka"));
  }

  @GameBean(order = 2)
  public KafkaMailSender kafkaMailSender(
      MailRepository mailRepository,
      KafkaProducer<String, CreateMailDto> producer,
      MailMapper mapper,
      PropertiesFactory propertiesFactory
  ) {
    KafkaMailSender sender = BasicComponentManager.getComponentCreator().create(KafkaMailSenderImpl.class, mailRepository, producer, mapper, propertiesFactory);
    sender.injectSelf(sender);
    return sender;
  }

}
