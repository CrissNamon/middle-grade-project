package ru.danilarassokhin.notification.command.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.danilarassokhin.cqrs.command.CommandHandler;
import ru.danilarassokhin.notification.command.CreateMailCommand;
import ru.danilarassokhin.notification.mapper.MailMapper;
import ru.danilarassokhin.messaging.dto.CreateMailDto;
import ru.danilarassokhin.notification.repository.command.MailNotificationCommandRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreateMailCommandHandler implements CommandHandler<CreateMailDto, Mono<Void>, CreateMailCommand> {

  private final MailNotificationCommandRepository repository;
  private final MailMapper mapper;

  @Override
  public Mono<Void> handle(CreateMailCommand action) {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .filter(Authentication::isAuthenticated)
        .flatMap(authentication -> {
          log.info("Is authenticated: {}", authentication.isAuthenticated());
          log.info("Executing command from: {}", authentication.getName());
          return Mono.just(action.dto());
        })
        .map(mapper::createMailDtoToEntity)
        .map(repository::save)
        .then();
  }

  @Override
  public Class<CreateMailCommand> getType() {
    return CreateMailCommand.class;
  }
}
