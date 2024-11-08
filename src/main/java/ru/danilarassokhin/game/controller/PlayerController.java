package ru.danilarassokhin.game.controller;

import lombok.RequiredArgsConstructor;
import ru.danilarassokhin.game.mapper.PlayerMapper;
import ru.danilarassokhin.game.model.request.CreatePlayerRequest;
import ru.danilarassokhin.game.server.annotation.PostRequest;
import ru.danilarassokhin.game.server.model.ResponseEntity;
import ru.danilarassokhin.game.service.PlayerService;
import tech.hiddenproject.progressive.annotation.Autofill;
import tech.hiddenproject.progressive.annotation.GameBean;

@GameBean
@RequiredArgsConstructor(onConstructor_ = {@Autofill})
public class PlayerController {

  private final PlayerService playerService;
  private final PlayerMapper playerMapper;

  @PostRequest("/api/player")
  public ResponseEntity createPlayer(CreatePlayerRequest request) {
    return ResponseEntity.ok(
        playerService.createPlayer(
            playerMapper.createPlayerRequestToDto(request)));
  }
}
