package ru.danilarassokhin.notification.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.danilarassokhin.notification.entity.MailNotificationEntity;
import ru.danilarassokhin.notification.service.MailNotificationService;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailNotificationServiceImpl implements MailNotificationService {

  private final JavaMailSender javaMailSender;

  @Override
  public Mono<Boolean> send(MailNotificationEntity mailNotification) {
    return Mono.just(mailNotification)
        .map(this::mailNotificationToSimpleMessage)
        .map(this::sendMessage);
  }

  private SimpleMailMessage mailNotificationToSimpleMessage(MailNotificationEntity mailNotification) {
    log.info("Processing mail: {}", mailNotification);
    var simpleMessage = new SimpleMailMessage();
    simpleMessage.setTo(mailNotification.getEmail());
    return simpleMessage;
  }

  private boolean sendMessage(SimpleMailMessage message) {
    try {
      log.info("Sending email from service");
      javaMailSender.send(message);
    } catch (RuntimeException e) {
      log.error("Error during sending email", e);
      return false;
    }
    return true;
  }

}
