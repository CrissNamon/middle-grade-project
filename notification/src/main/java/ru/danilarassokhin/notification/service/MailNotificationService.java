package ru.danilarassokhin.notification.service;

import reactor.core.publisher.Mono;
import ru.danilarassokhin.notification.entity.MailNotificationEntity;

public interface MailNotificationService {

  Mono<Boolean> send(MailNotificationEntity mailNotification);
}
