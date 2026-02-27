package ru.danilarassokhin.statistic.service;

import org.apache.commons.lang3.NotImplementedException;
import reactor.core.publisher.Flux;

/**
 * Работает с потоком уведомлений.
 * @param <T> - Тип уведомления
 */
public interface FeedService<T> {

  /**
   * @return Возврвщает поток всех уведомлений.
   */
  default Flux<T> getAll() {
    throw new NotImplementedException();
  }

  /**
   * @param id Идентификатор группы
   * @return Возвращает поток уведомлений для указанной группы.
   */
  default Flux<T> getById(Integer id) {
    throw new NotImplementedException();
  }

}
