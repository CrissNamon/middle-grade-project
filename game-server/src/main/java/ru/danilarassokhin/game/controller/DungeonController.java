package ru.danilarassokhin.game.controller;

import lombok.RequiredArgsConstructor;
import ru.danilarassokhin.game.mapper.DungeonMapper;
import ru.danilarassokhin.game.model.request.CreateDungeonRequest;
import ru.danilarassokhin.game.model.request.DungeonAttackRequest;
import ru.danilarassokhin.server.annotation.PostRequest;
import ru.danilarassokhin.server.model.ResponseEntity;
import ru.danilarassokhin.game.service.DungeonService;
import tech.hiddenproject.progressive.annotation.Autofill;
import tech.hiddenproject.progressive.annotation.GameBean;

@GameBean
@RequiredArgsConstructor(onConstructor_ = {@Autofill})
public class DungeonController {

  private final DungeonService dungeonService;
  private final DungeonMapper dungeonMapper;

  @PostRequest("/api/dungeon")
  public ResponseEntity createDungeon(CreateDungeonRequest request) {
    return ResponseEntity.ok(dungeonService.save(dungeonMapper.createDungeonRequestToDto(request)));
  }

  @PostRequest("/api/fight")
  public ResponseEntity attackDungeon(DungeonAttackRequest request) {
    return ResponseEntity.ok(dungeonService.attack(dungeonMapper.createDungeonLogDtoFromRequest(request)));
  }

}
