package ru.danilarassokhin.game.worker.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import ru.danilarassokhin.game.entity.MailEntity;
import ru.danilarassokhin.game.mapper.MailMapper;
import ru.danilarassokhin.injection.exception.ApplicationException;
import ru.danilarassokhin.messaging.dto.CreateMailDto;
import ru.danilarassokhin.util.PropertiesFactory;
import tech.hiddenproject.progressive.annotation.Autofill;

@RequiredArgsConstructor(onConstructor_ = @Autofill)
@Slf4j
public class KafkaMailSenderImpl implements KafkaMailSender {

  private static final String MAIL_TOPIC_PROPERTY = "app.topic.mail";

  private final Producer<String, CreateMailDto> kafkaProducer;
  private final MailMapper mapper;
  private final PropertiesFactory propertiesFactory;

  private String topic;

  @Override
  public void send(MailEntity mailEntity) {
    try {
      log.info("Found new message: {}", mailEntity);
      sendMail(mapper.mailEntityToCreateMailDto(mailEntity));
    } catch (RuntimeException e) {
      log.error("Error sending message", e);
      throw e;
    }
  }

  private void sendMail(CreateMailDto createMailDto) {
    log.info("Sending to topic {}", topic);
    kafkaProducer.send(new ProducerRecord<>(topic, createMailDto));
  }

  @Autofill
  public void setTopic() {
    this.topic = propertiesFactory.getAsString(MAIL_TOPIC_PROPERTY)
        .orElseThrow(() -> new ApplicationException("Mail topic is not defined"));
  }

}
