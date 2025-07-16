package ru.danilarassokhin.notification.command.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.danilarassokhin.cqrs.command.CommandHandler;
import ru.danilarassokhin.notification.command.CreateMailCommand;
import ru.danilarassokhin.notification.message.dto.CreateMailDto;
import ru.danilarassokhin.notification.repository.command.MailNotificationCommandRepository;

@Component
@RequiredArgsConstructor
public class CreateMailCommandHandler implements CommandHandler<CreateMailDto, CreateMailCommand> {

  private final MailNotificationCommandRepository repository;

  @Override
  public Void handle(CreateMailCommand action) {

    return null;
  }

  @Override
  public Class<CreateMailCommand> getType() {
    return CreateMailCommand.class;
  }
}
