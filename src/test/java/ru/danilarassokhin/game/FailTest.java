package ru.danilarassokhin.game;

import org.junit.jupiter.api.Test;

public class FailTest {

  @Test
  public void failTest() {
    throw new RuntimeException("");
  }

}
