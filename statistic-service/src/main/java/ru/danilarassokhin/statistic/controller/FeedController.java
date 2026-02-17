package ru.danilarassokhin.statistic.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import ru.danilarassokhin.statistic.dto.FeedDto;
import ru.danilarassokhin.statistic.service.impl.GlobalFeedService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class FeedController {

  private final GlobalFeedService globalFeedService;

  @GetMapping(value = "/feed", produces = MediaType.TEXT_EVENT_STREAM_VALUE, consumes = MediaType.ALL_VALUE)
  public Flux<FeedDto> getFeed() {
    return globalFeedService.getAll();
  }

}
