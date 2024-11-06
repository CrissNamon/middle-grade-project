package ru.danilarassokhin.game.controller;

import lombok.RequiredArgsConstructor;
import ru.danilarassokhin.game.exception.ApplicationException;
import ru.danilarassokhin.game.model.CreatePlayerDto;
import ru.danilarassokhin.game.server.annotation.PostRequest;
import ru.danilarassokhin.game.server.model.ResponseEntity;
import ru.danilarassokhin.game.service.PlayerService;
import tech.hiddenproject.progressive.annotation.Autofill;
import tech.hiddenproject.progressive.annotation.GameBean;

@GameBean
@RequiredArgsConstructor(onConstructor_ = {@Autofill})
public class PlayerController {

  private final PlayerService playerService;

  @PostRequest("/api/player")
  public ResponseEntity createPlayer(CreatePlayerDto createPlayerDto) {
    try {
      playerService.save(createPlayerDto);
      return ResponseEntity.ok();
    } catch (ApplicationException e) {
      return ResponseEntity.ok(e.getMessage());
    }
  }

}
