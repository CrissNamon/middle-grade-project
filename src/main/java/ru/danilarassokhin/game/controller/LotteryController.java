package ru.danilarassokhin.game.controller;

import lombok.RequiredArgsConstructor;
import ru.danilarassokhin.game.server.annotation.PostRequest;
import ru.danilarassokhin.game.server.model.RequestEntity;
import ru.danilarassokhin.game.server.model.ResponseEntity;
import ru.danilarassokhin.game.service.LotteryService;
import tech.hiddenproject.progressive.annotation.Autofill;
import tech.hiddenproject.progressive.annotation.GameBean;

@GameBean
@RequiredArgsConstructor(onConstructor_ = {@Autofill})
public class LotteryController {

  private final LotteryService lotteryService;

  @PostRequest("/api/lottery/{playerId}")
  public ResponseEntity attendLottery(RequestEntity request) {
    lotteryService.attendLottery(request.getPathParameter("playerId"));
    return ResponseEntity.ok();
  }

}
