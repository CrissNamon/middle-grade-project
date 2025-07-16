package ru.danilarassokhin.notification.command.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.danilarassokhin.cqrs.command.CommandHandler;
import ru.danilarassokhin.notification.command.CreateMailCommand;
import ru.danilarassokhin.notification.mapper.MailMapper;
import ru.danilarassokhin.messaging.model.CreateMailDto;
import ru.danilarassokhin.notification.repository.command.MailNotificationCommandRepository;

@Component
@RequiredArgsConstructor
public class CreateMailCommandHandler implements CommandHandler<CreateMailDto, CreateMailCommand> {

  private final MailNotificationCommandRepository repository;
  private final MailMapper mapper;

  @Override
  public Void handle(CreateMailCommand action) {
    repository.save(mapper.createMailDtoToEntity(action.dto())).subscribe();
    return null;
  }

  @Override
  public Class<CreateMailCommand> getType() {
    return CreateMailCommand.class;
  }
}
