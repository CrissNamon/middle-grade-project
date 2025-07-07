package ru.danilarassokhin.game.entity.camunda;

import lombok.RequiredArgsConstructor;

/**
 * Contains game signal ids for Camunda.
 */
@RequiredArgsConstructor
public enum CamundaSignal {

  /**
   * Broadcast when dungeon is completed.
   */
  s_dungeon_completed("_%s");

  private final String postfixPattern;

  public String create(Object... patternVariables) {
    return name() + String.format(postfixPattern, patternVariables);
  }

}
