package ru.danilarassokhin.notification.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import ru.danilarassokhin.notification.entity.MailNotificationEntity;
import ru.danilarassokhin.notification.repository.MailNotificationRepository;

@RestController
@RequestMapping("/mail")
@RequiredArgsConstructor
public class NotificationController {

  private final MailNotificationRepository mailNotificationRepository;

  @GetMapping
  public Flux<MailNotificationEntity> getAllFroSend() {
    return mailNotificationRepository.findAllForSend();
  }

}
