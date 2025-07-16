package ru.danilarassokhin.game.worker.kafka;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import ru.danilarassokhin.game.mapper.MailMapper;
import ru.danilarassokhin.messaging.model.CreateMailDto;
import ru.danilarassokhin.game.repository.MailRepository;
import ru.danilarassokhin.injection.exception.ApplicationException;
import ru.danilarassokhin.sql.annotation.Transactional;
import ru.danilarassokhin.util.PropertiesFactory;
import tech.hiddenproject.progressive.annotation.Autofill;
import tech.hiddenproject.progressive.annotation.GameBean;

@GameBean
@RequiredArgsConstructor(onConstructor_ = @Autofill)
@Slf4j
public class KafkaMailSenderImpl implements KafkaMailSender {

  private static final int THREADS_COUNT = 2;
  private static final int QUEUE_SIZE = 2;

  private final MailRepository mailRepository;
  private final KafkaProducer<String, CreateMailDto> kafkaProducer;
  private final MailMapper mapper;
  private final PropertiesFactory propertiesFactory;

  private KafkaMailSender self;

  private final ThreadPoolExecutor threadPoolExecutor =
      new ThreadPoolExecutor(THREADS_COUNT, THREADS_COUNT, 5, TimeUnit.MINUTES, new LinkedBlockingQueue<>(QUEUE_SIZE),
                             new ThreadPoolExecutor.CallerRunsPolicy());

  @Transactional
  public void schedule() {
    System.out.println("STARTED SCHEDULER");
    var topic = propertiesFactory.getAsString("app.topic.mail")
        .orElseThrow(() -> new ApplicationException("Mail topic is not defined"));
    log.info("Searching for new mails");
    mailRepository.selectOneForSend()
        .map(mapper::mailEntityToCreateMailDto)
        .ifPresentOrElse(createMailDto -> {
          System.out.println("FOUND MESSAGE");
          kafkaProducer.send(new ProducerRecord<>(topic, createMailDto));
          System.out.println("Sent message");
        }, () -> System.out.println("NOT FOUND MESSAGE"));
  }

  @Override
  public void injectSelf(KafkaMailSender kafkaMailSender) {
    this.self = kafkaMailSender;
    self.schedule();
  }

}
