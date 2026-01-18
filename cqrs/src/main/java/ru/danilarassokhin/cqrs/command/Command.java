package ru.danilarassokhin.cqrs.command;

import ru.danilarassokhin.cqrs.Action;

/**
 * Действие с типоп Команда.
 * @param <I> Тип входного параметра
 * @param <O> Тип результата
 */
public interface Command<I, O> extends Action<I, O> {

}
