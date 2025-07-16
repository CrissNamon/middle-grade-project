package ru.danilarassokhin.notification.command.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.danilarassokhin.cqrs.command.CommandHandler;
import ru.danilarassokhin.notification.command.SendMailCommand;
import ru.danilarassokhin.notification.entity.MailNotificationEntity;
import ru.danilarassokhin.notification.repository.command.MailNotificationCommandRepository;
import ru.danilarassokhin.notification.service.MailNotificationService;

@Component
@RequiredArgsConstructor
public class SendMailCommandHandler implements CommandHandler<MailNotificationEntity, SendMailCommand> {

  private final MailNotificationService service;
  private final MailNotificationCommandRepository repository;

  @Override
  public Void handle(SendMailCommand action) {
     service.send(action.mailNotification())
        .map(action.mailNotification()::setProcessed)
        .flatMap(repository::save)
        .subscribe();
     return null;
  }

  @Override
  public Class<SendMailCommand> getType() {
    return SendMailCommand.class;
  }
}
