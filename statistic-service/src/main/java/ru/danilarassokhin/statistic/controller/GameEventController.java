package ru.danilarassokhin.statistic.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ru.danilarassokhin.statistic.dto.GameEventDto;
import ru.danilarassokhin.statistic.dto.GameEventFilter;
import ru.danilarassokhin.statistic.service.GameEventService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events")
public class GameEventController {

  private final GameEventService gameEventService;

  @GetMapping
  public Mono<List<GameEventDto>> getEvents(GameEventFilter filter) {
    return Mono.fromCallable(() -> gameEventService.findByFilters(filter))
        .subscribeOn(Schedulers.boundedElastic());
  }
}
