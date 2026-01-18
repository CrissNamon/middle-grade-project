package ru.danilarassokhin.cqrs.command;

/**
 * Хранит обработчики {@link Command} и делегирует им выполнение.
 */
public interface CommandMediator {

  /**
   * Ищет обработчик и передаем ему команду.
   * @param command {@link Command}
   * @param <I> Тип входного параметра
   * @param <O> Тип результата
   */
  <I, O> O execute(Command<I, O> command);

}
