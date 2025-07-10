package ru.danilarassokhin.cqrs.command;

import ru.danilarassokhin.cqrs.Action;

/**
 * Действие с типоп Команда.
 * @param <I> Тип входного параметра
 */
public interface Command<I> extends Action<I, Void> {

}
