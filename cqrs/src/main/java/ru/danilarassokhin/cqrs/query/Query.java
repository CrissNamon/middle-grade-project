package ru.danilarassokhin.cqrs.query;

import ru.danilarassokhin.cqrs.Action;

/**
 * Тип действия с типом Запрос.
 * @param <I> Тип входного параметра
 * @param <O> Тип выходного параметра
 */
public interface Query<I, O> extends Action<I, O> {

}
