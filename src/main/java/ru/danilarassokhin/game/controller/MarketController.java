package ru.danilarassokhin.game.controller;

import lombok.RequiredArgsConstructor;
import ru.danilarassokhin.game.model.request.BuyItemRequest;
import ru.danilarassokhin.game.server.annotation.PostRequest;
import ru.danilarassokhin.game.server.model.ResponseEntity;
import ru.danilarassokhin.game.service.MarketService;
import tech.hiddenproject.progressive.annotation.Autofill;
import tech.hiddenproject.progressive.annotation.GameBean;

@GameBean
@RequiredArgsConstructor(onConstructor_ = {@Autofill})
public class MarketController {

  private final MarketService marketService;

  @PostRequest("/api/market")
  public ResponseEntity butItem(BuyItemRequest request) {
    marketService.buyItem(request.playerId(), request.itemId());
    return ResponseEntity.ok();
  }

}
