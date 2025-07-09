package ru.danilarassokhin.cqrs.query;

/**
 * Хранит обработчики {@link Query} и делегирует им выполнение.
 */
public interface QueryMediator {

  /**
   * Ищет обработчик и передаем ему запрос.
   * @param query {@link Query}
   * @param <I> Тип входного параметра
   * @param <O> Тип выходного параметра
   */
  <I, O> O execute(Query<I, O> query);

}
