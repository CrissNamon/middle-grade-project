package ru.danilarassokhin.game.worker.kafka;

import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import ru.danilarassokhin.game.entity.MailEntity;
import ru.danilarassokhin.game.mapper.MailMapper;
import ru.danilarassokhin.messaging.dto.CreateMailDto;
import ru.danilarassokhin.injection.exception.ApplicationException;
import ru.danilarassokhin.sql.service.TransactionContext;
import ru.danilarassokhin.sql.service.TransactionManager;
import ru.danilarassokhin.util.PropertiesFactory;
import tech.hiddenproject.progressive.annotation.Autofill;
import tech.hiddenproject.progressive.annotation.GameBean;

@GameBean
@RequiredArgsConstructor(onConstructor_ = @Autofill)
@Slf4j
public class KafkaMailSenderImpl implements KafkaMailSender {

  private static final String FIND_ONE_FOR_SEND_QUERY =
      String.format("SELECT * FROM %s LIMIT 1 FOR UPDATE SKIP LOCKED", MailEntity.TABLE_NAME);
  private static final String SET_PROCESSED_QUERY =
      String.format("UPDATE %s SET is_processed = true WHERE id = ?", MailEntity.TABLE_NAME);

  private final ScheduledExecutorService threadPoolExecutor =
      Executors.newSingleThreadScheduledExecutor();

  private final KafkaProducer<String, CreateMailDto> kafkaProducer;
  private final MailMapper mapper;
  private final PropertiesFactory propertiesFactory;
  private final TransactionManager transactionManager;

  private String topic;

  public void schedule() {
    log.info("Searching for new messages");
    threadPoolExecutor.scheduleWithFixedDelay(() -> {
      transactionManager.doInTransaction(ctx -> {
        findOneForSend(ctx).ifPresent(mailEntity -> trySendMail(mailEntity, ctx));
      });
    }, 5, 5, TimeUnit.SECONDS);
  }

  private void trySendMail(MailEntity mailEntity, TransactionContext ctx) {
    try {
      log.info("Found new message: {}", mailEntity);
      var dto = mapper.mailEntityToCreateMailDto(mailEntity);
      markProcessed(mailEntity, ctx);
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

  private Optional<MailEntity> findOneForSend(TransactionContext ctx) {
    return Optional.ofNullable(ctx.query(FIND_ONE_FOR_SEND_QUERY).fetchOne(MailEntity.class));
  }

  private void markProcessed(MailEntity mailEntity, TransactionContext ctx) {
    ctx.query(SET_PROCESSED_QUERY, mailEntity.id()).execute();
  }

  @Autofill
  public void setTopic() {
    this.topic = propertiesFactory.getAsString("app.topic.mail")
        .orElseThrow(() -> new ApplicationException("Mail topic is not defined"));
  }

}
