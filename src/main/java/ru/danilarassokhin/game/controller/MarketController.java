package ru.danilarassokhin.game.controller;

import lombok.RequiredArgsConstructor;
import ru.danilarassokhin.game.mapper.MarketMapper;
import ru.danilarassokhin.game.model.request.BuyItemRequest;
import ru.danilarassokhin.game.model.request.CreateMarketItemRequest;
import ru.danilarassokhin.game.server.annotation.PostRequest;
import ru.danilarassokhin.game.server.model.ResponseEntity;
import ru.danilarassokhin.game.service.MarketService;
import tech.hiddenproject.progressive.annotation.Autofill;
import tech.hiddenproject.progressive.annotation.GameBean;

@GameBean
@RequiredArgsConstructor(onConstructor_ = {@Autofill})
public class MarketController {

  private final MarketService marketService;
  private final MarketMapper marketMapper;

  @PostRequest("/api/market")
  public ResponseEntity buyItem(BuyItemRequest request) {
    marketService.buy(request.playerId(), request.itemId());
    return ResponseEntity.ok();
  }

  @PostRequest("/api/market/item")
  public ResponseEntity createItem(CreateMarketItemRequest createMarketItemRequest) {
    var dto = marketMapper.createMarketItemRequestToDto(createMarketItemRequest);
    return ResponseEntity.ok(marketService.create(dto));
  }

}
