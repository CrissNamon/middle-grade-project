package ru.danilarassokhin.cqrs;

/**
 * Обработчик для действия.
 * @param <I> Входной параметр {@link Action}
 * @param <O> Выходной параметр {@link Action}
 * @param <A> Тип действия.
 */
public interface ActionHandler<I, O, A extends Action<I, O>> {

  /**
   * Выполняет переданное действие.
   * @param action {@link Action}
   * @return Результат выполнения
   */
  O handle(A action);

  /**
   * @return Тип действия, которое поддерживает обработчик
   */
  Class<A> getType();

}
