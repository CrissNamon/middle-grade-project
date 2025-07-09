package ru.danilarassokhin.cqrs.query;

import ru.danilarassokhin.cqrs.ActionHandler;

/**
 * Обработчик для запросов {@link Query}.
 * @param <I> Тип входного параметра
 * @param <O> Тип выходного параметра
 * @param <Q> Тип комманды
 */
public interface QueryHandler<I, O, Q extends Query<I, O>> extends ActionHandler<I, O, Q> {

}
