package ru.danilarassokhin.cqrs.command;

import ru.danilarassokhin.cqrs.ActionHandler;

/**
 * Обработчик для команд {@link Command}.
 * @param <I> Тип входного параметра
 * @param <O> Тип результата
 * @param <C> Тип комманды
 */
public interface CommandHandler<I, O, C extends Command<I, O>> extends ActionHandler<I, O, C> {

}
