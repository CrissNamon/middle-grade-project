package ru.danilarassokhin.game.worker.kafka;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import ru.danilarassokhin.game.entity.MailEntity;
import ru.danilarassokhin.game.mapper.MailMapper;
import ru.danilarassokhin.game.repository.MailRepository;
import ru.danilarassokhin.messaging.dto.CreateMailDto;
import ru.danilarassokhin.injection.exception.ApplicationException;
import ru.danilarassokhin.sql.service.TransactionManager;
import ru.danilarassokhin.util.PropertiesFactory;
import tech.hiddenproject.progressive.annotation.Autofill;
import tech.hiddenproject.progressive.annotation.GameBean;

@GameBean
@RequiredArgsConstructor(onConstructor_ = @Autofill)
@Slf4j
public class KafkaMailSenderImpl implements KafkaMailSender {

  private final static Long SENDING_DELAY_MINUTES = 5L;

  private final ScheduledExecutorService threadPoolExecutor =
      Executors.newSingleThreadScheduledExecutor();

  private final KafkaProducer<String, CreateMailDto> kafkaProducer;
  private final MailMapper mapper;
  private final PropertiesFactory propertiesFactory;
  private final TransactionManager transactionManager;
  private final MailRepository mailRepository;

  private String topic;

  public void schedule() {
    log.info("Searching for new messages");
    threadPoolExecutor.scheduleWithFixedDelay(() -> {
      transactionManager.doInTransaction(ctx -> {
        mailRepository.findOneForSend(ctx)
            .ifPresent(mailEntity -> {
              mailRepository.markProcessed(mailEntity, ctx);
              trySendMail(mailEntity);
            });
      });
    }, SENDING_DELAY_MINUTES, SENDING_DELAY_MINUTES, TimeUnit.MINUTES);
  }

  private void trySendMail(MailEntity mailEntity) {
    try {
      log.info("Found new message: {}", mailEntity);
      var dto = mapper.mailEntityToCreateMailDto(mailEntity);
      sendMail(dto);
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
    this.topic = propertiesFactory.getAsString("app.topic.mail")
        .orElseThrow(() -> new ApplicationException("Mail topic is not defined"));
    schedule();
  }

}
