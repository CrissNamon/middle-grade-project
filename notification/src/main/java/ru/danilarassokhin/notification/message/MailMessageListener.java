package ru.danilarassokhin.notification.message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.danilarassokhin.cqrs.command.CommandMediator;
import ru.danilarassokhin.notification.command.CreateMailCommand;
import ru.danilarassokhin.notification.exception.MailListenerException;
import ru.danilarassokhin.notification.message.dto.CreateMailDto;

@Component
@RequiredArgsConstructor
@Slf4j
public class MailMessageListener {

  private final CommandMediator commandMediator;

  @RetryableTopic(kafkaTemplate = "retryableKafkaTemplate", attempts = "5",
                  backoff = @Backoff(delay = 3000, multiplier = 1.5, maxDelay = 15000))
  @KafkaListener(topics = "${mail.kafka.topic}")
  @Transactional
  public void consume(@Payload CreateMailDto createMailDto, Acknowledgment acknowledgment) {
    try {
      log.info("Received message: {}", createMailDto);
      commandMediator.execute(new CreateMailCommand(createMailDto));
      acknowledgment.acknowledge();
    } catch (RuntimeException e) {
      log.error("Error occurred during message processing", e);
      throw new MailListenerException(e);
    }
  }

}
