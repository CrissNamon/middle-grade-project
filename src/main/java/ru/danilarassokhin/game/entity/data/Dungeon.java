package ru.danilarassokhin.game.entity.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Dungeon {

  SEWERS(2);

  private final Integer health;

}
