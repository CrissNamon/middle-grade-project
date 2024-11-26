package ru.danilarassokhin.game.entity.camunda;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Variables for Camunda process.
 */
@RequiredArgsConstructor
@Getter
public enum CamundaVariables {

  BUSINESS_KEY("businessKey"),
  LEVEL("level");

  private final String camundaVariableName;

}
