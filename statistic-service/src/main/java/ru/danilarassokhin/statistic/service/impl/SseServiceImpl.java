package ru.danilarassokhin.statistic.service.impl;

import java.time.Duration;

import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import ru.danilarassokhin.statistic.service.SseService;

@Service
public class SseServiceImpl implements SseService {

  /**
   * Превращает оригинальный {@link Flux} в {@link Flux} из SSE,
   * который держит соединение открытым бесконечно.
   */
  @Override
  public <T> Flux<ServerSentEvent<T>> createSseFlux(Flux<T> original) {
    return createHeartbeatFlux(Duration.ofSeconds(5), original);
  }

  private <T> Flux<ServerSentEvent<T>> createHeartbeatFlux(Duration duration, Flux<T> data) {
    Flux<ServerSentEvent<T>> heartBeat = Flux.interval(duration).map(this::createHeartbeat);
    return data.map(this::createSseEvent).mergeWith(heartBeat);
  }

  private <T> ServerSentEvent<T> createHeartbeat(Object any) {
    return ServerSentEvent.<T>builder().comment("keep-alive heartbeat").build();
  }

  private <T> ServerSentEvent<T> createSseEvent(T data) {
    return ServerSentEvent.builder(data).build();
  }
}
