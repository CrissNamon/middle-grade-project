package ru.danilarassokhin.cqrs.command.impl;

import java.util.List;

import ru.danilarassokhin.cqrs.command.Command;
import ru.danilarassokhin.cqrs.command.CommandExecutor;
import ru.danilarassokhin.cqrs.command.CommandHandler;
import ru.danilarassokhin.cqrs.impl.ActionExecutorImpl;

public class CommandExecutorImpl implements CommandExecutor {

  private final ActionExecutorImpl<Command<?>> delegate;

  public CommandExecutorImpl(List<? extends CommandHandler<?, ?>> handlers) {
    this.delegate = new ActionExecutorImpl<>(handlers);
  }

  @Override
  public <I> void execute(Command<I> command) {
    delegate.execute(command);
  }
}
