package ru.danilarassokhin.statistic.service;

import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

/**
 * Сервис для работы с SSE.
 */
public interface SseService {

  /**
   * Превращает оригинальный {@link Flux} в {@link Flux} из SSE.
   */
  <T> Flux<ServerSentEvent<T>> createSseFlux(Flux<T> original);

}
