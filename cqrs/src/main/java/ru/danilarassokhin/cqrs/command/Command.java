package ru.danilarassokhin.cqrs.command;

import ru.danilarassokhin.cqrs.Action;

public interface Command<I> extends Action<I, Void> {

}
