package ru.danilarassokhin.cqrs.command;

import ru.danilarassokhin.cqrs.ActionHandler;

public interface CommandHandler<I, C extends Command<I>> extends ActionHandler<I, Void, C> {

}
