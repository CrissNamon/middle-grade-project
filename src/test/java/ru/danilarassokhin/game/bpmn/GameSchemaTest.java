package ru.danilarassokhin.game.bpmn;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.danilarassokhin.game.entity.camunda.CamundaAction;
import ru.danilarassokhin.game.entity.camunda.CamundaSignal;

public class GameSchemaTest extends BpmnSchemaTestBase {

  private static final String FIRST_USER_TASK = "Activity_17jyf9v";
  private static final String RETURN_TO_MENU_FROM_INVENTORY_TASK = "Activity_1ihhazn";
  private static final String IN_DUNGEON_TASK = "Activity_08s7q7p";
  private static final String ATTACK_DELEGATE = "Activity_0leqp75";
  private static final String GET_DUNGEON_DELEGATE = "Activity_1uocarl";
  private static final String DUNGEON_SUBPROCESS = "Activity_0f6n6yi";
  private static final String ATTACK_JOB_TYPE = "attack";
  private static final Integer LEVEL_NUMBER = 1;
  private static final String LEVEL_VARIABLE_NAME = "level";
  private static final ImmutablePair<String, Object> LEVEL_VARIABLE =
      ImmutablePair.of(LEVEL_VARIABLE_NAME, LEVEL_NUMBER.toString());

  @Test
  @DisplayName("it should wait at main menu after start")
  public void itShouldWaitAtUserTaskAfterStart() {
    bpmnProcess(processAssertions -> {
      processAssertions.startProcess(LEVEL_VARIABLE);
      processAssertions.assertStarted();
      processAssertions.assertWaitingAtExactly(FIRST_USER_TASK);
    });
  }

  @Test
  @DisplayName("User do action m_inventory, process goes to next user task")
  public void itShouldGoToInventoryOnUserTaskComplete() {
    bpmnProcess(processAssertions -> {
      processAssertions.startProcess(LEVEL_VARIABLE);
      processAssertions.assertStarted();
      processAssertions.assertWaitingAtExactly(FIRST_USER_TASK);
      processAssertions.doAction(CamundaAction.m_inventory);
      processAssertions.assertWaitingAtExactly(RETURN_TO_MENU_FROM_INVENTORY_TASK);
    });
  }

  @Test
  @DisplayName("User do action m_dungeon, process retrieves dungeon")
  public void itShouldGoToDungeonOnUserTaskComplete() {
    bpmnProcess(processAssertions -> {
      processAssertions.startProcess(LEVEL_VARIABLE);
      processAssertions.assertStarted();
      processAssertions.assertWaitingAtExactly(FIRST_USER_TASK);
      processAssertions.doAction(CamundaAction.m_dungeon);
      processAssertions.assertWaitingAtExactly(GET_DUNGEON_DELEGATE);
    });
  }

  @Test
  @DisplayName("Process goes to dungeon after invoking getDungeon worker")
  public void itShouldGoToDungeonAfterGetDungeonDelegate() {
    bpmnProcess(processAssertions -> {
      processAssertions.startProcessBefore(DUNGEON_SUBPROCESS, LEVEL_VARIABLE);
      processAssertions.assertStarted();
      processAssertions.assertWaitingAtExactly(IN_DUNGEON_TASK);
    });
  }

  @Test
  @DisplayName("User do action m_main_menu in dungeon, process returns to main menu")
  public void itShouldGoToMainMenuFromDungeon() {
    bpmnProcess(processAssertions -> {
      processAssertions.startProcessBefore(DUNGEON_SUBPROCESS, LEVEL_VARIABLE);
      processAssertions.assertStarted();
      processAssertions.assertWaitingAtExactly(IN_DUNGEON_TASK);
      processAssertions.doAction(CamundaAction.m_main_menu);
      processAssertions.assertWaitingAtExactly(FIRST_USER_TASK);
    });
  }

  @Test
  @DisplayName("User do action m_attack in dungeon, process call attack worker and stays in dungeon")
  public void itShouldAttackFromDungeon() {
    bpmnProcess(processAssertions -> {
      processAssertions.startProcessBefore(DUNGEON_SUBPROCESS, LEVEL_VARIABLE);
      processAssertions.assertStarted();
      processAssertions.assertWaitingAtExactly(IN_DUNGEON_TASK);
      processAssertions.doAction(CamundaAction.m_attack);
      processAssertions.assertWaitingAtExactly(ATTACK_DELEGATE);
      processAssertions.executeJob(ATTACK_JOB_TYPE);
      processAssertions.assertWaitingAtExactly(IN_DUNGEON_TASK);
    });
  }

  @Test
  @DisplayName("Process receives s_dungeon_completed signal while in dungeon, process returns to main menu")
  public void itShouldReturnToMainMenuOnSignal() {
    bpmnProcess(processAssertions -> {
      processAssertions.startProcessBefore(DUNGEON_SUBPROCESS, LEVEL_VARIABLE);
      processAssertions.assertStarted();
      processAssertions.assertWaitingAtExactly(IN_DUNGEON_TASK);
      processAssertions.sendSignal(CamundaSignal.s_dungeon_completed.create(LEVEL_NUMBER));
      processAssertions.assertWaitingAtExactly(FIRST_USER_TASK);
    });
  }

  @Test
  @DisplayName("Process receives s_dungeon_completed signal while in inventory, process does not return to main menu when not in dungeon")
  public void itShouldNotReturnToMainMenuOnSignalWhenNotInDungeon() {
    bpmnProcess(processAssertions -> {
      processAssertions.startProcessBefore(RETURN_TO_MENU_FROM_INVENTORY_TASK, LEVEL_VARIABLE);
      processAssertions.assertStarted();
      processAssertions.assertWaitingAtExactly(RETURN_TO_MENU_FROM_INVENTORY_TASK);
      processAssertions.sendSignal(CamundaSignal.s_dungeon_completed.create(LEVEL_NUMBER));
      processAssertions.assertWaitingAtExactly(RETURN_TO_MENU_FROM_INVENTORY_TASK);
    });
  }

}
