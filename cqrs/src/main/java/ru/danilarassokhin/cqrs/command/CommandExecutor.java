package ru.danilarassokhin.cqrs.command;

public interface CommandExecutor {

  <I> void execute(Command<I> query);

}
