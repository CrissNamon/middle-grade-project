package ru.danilarassokhin.statistic.controller;

import java.time.LocalDateTime;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ru.danilarassokhin.statistic.dto.GameEventDto;
import ru.danilarassokhin.statistic.service.GameEventService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events")
public class GameEventController {

  private final GameEventService gameEventService;

  @GetMapping
  public Mono<List<GameEventDto>> getEvents(
      @RequestParam(required = false) String type,
      @RequestParam(required = false) Integer playerId,
      @RequestParam(required = false) Integer bossId,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
      @RequestParam(required = false, defaultValue = "100") Integer limit
  ) {
    return Mono.fromCallable(() ->
        gameEventService.findByFilters(type, playerId, bossId, from, to, limit)
    ).subscribeOn(Schedulers.boundedElastic());
  }
}
