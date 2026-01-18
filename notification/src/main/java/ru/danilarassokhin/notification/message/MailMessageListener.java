package ru.danilarassokhin.notification.message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import ru.danilarassokhin.cqrs.command.CommandMediator;
import ru.danilarassokhin.messaging.dto.CreateMailDto;
import ru.danilarassokhin.messaging.dto.KafkaHeader;
import ru.danilarassokhin.notification.annotation.ReactiveAuthorized;
import ru.danilarassokhin.notification.command.CreateMailCommand;

@Component
@RequiredArgsConstructor
@Slf4j
public class MailMessageListener {

  private final CommandMediator commandMediator;

  @ReactiveAuthorized("#bearerToken")
  @RetryableTopic(kafkaTemplate = "retryableKafkaTemplate", attempts = "5",
                  backoff = @Backoff(delay = 3000, multiplier = 1.5, maxDelay = 15000))
  @KafkaListener(topics = "${mail.kafka.topic}")
  @Transactional
  public Mono<Void> consume(
      @Payload CreateMailDto createMailDto,
      @Header(value = KafkaHeader.AUTHENTICATION) String bearerToken,
      Acknowledgment acknowledgment
  ) {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .doOnNext(authentication -> log.info("Checking authentication inside MailMessageListener"))
        .filter(Authentication::isAuthenticated)
        .then(Mono.just(new CreateMailCommand(createMailDto)))
        .flatMap(commandMediator::execute)
        .doOnSuccess(cmd -> acknowledgment.acknowledge())
        .doOnError(t -> log.error("Error occurred", t))
        .then();
  }

}
