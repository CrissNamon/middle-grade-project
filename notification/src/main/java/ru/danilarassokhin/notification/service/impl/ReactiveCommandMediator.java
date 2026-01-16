package ru.danilarassokhin.notification.service.impl;

import java.util.List;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import ru.danilarassokhin.cqrs.command.Command;
import ru.danilarassokhin.cqrs.command.CommandHandler;
import ru.danilarassokhin.cqrs.command.CommandMediator;
import ru.danilarassokhin.cqrs.impl.ActionMediator;

@RequiredArgsConstructor
public class ReactiveCommandMediator implements CommandMediator {

  private final ActionMediator delegate;

  public ReactiveCommandMediator(List<? extends CommandHandler<?, ?, ?>> handlers) {
    this.delegate = new ActionMediator(handlers);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <I, O> O execute(Command<I, O> command) {
     return (O) Mono.just(command)
        .flatMap(cmd -> {
          var result = delegate.execute(cmd);
          if (result instanceof Mono<?>) {
            return (Mono<?>) result;
          }
          return Mono.just(result);
        });
  }
}
