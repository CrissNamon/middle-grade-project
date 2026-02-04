package ru.danilarassokhin.cqrs.command.impl;

import java.util.List;

import ru.danilarassokhin.cqrs.command.Command;
import ru.danilarassokhin.cqrs.command.CommandMediator;
import ru.danilarassokhin.cqrs.command.CommandHandler;
import ru.danilarassokhin.cqrs.impl.ActionMediator;

public class CommandMediatorImpl implements CommandMediator {

  private final ActionMediator delegate;

  public CommandMediatorImpl(List<? extends CommandHandler<?, ?, ?>> handlers) {
    this.delegate = new ActionMediator(handlers);
  }

  @Override
  public <I, O> O execute(Command<I, O> command) {
    return delegate.execute(command);
  }
}
