package ru.danilarassokhin.game.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.danilarassokhin.game.model.dto.ActionDto;
import ru.danilarassokhin.game.model.dto.BpmnFlowNodeType;
import ru.danilarassokhin.game.model.dto.SequenceActionDto;

public class BpmnServiceImplTest {

  private static final Integer TEST_BPMN_VERSION = 1;

  private final BpmnServiceImpl bpmnService = new BpmnServiceImpl(List.of("bpmn/test.bpmn"));

  @ParameterizedTest
  @MethodSource("findEventsArguments")
  public void itShouldFindEventsAfterGateway(List<SequenceActionDto> currentFlow, Set<ActionDto> expected) {
    var actual = bpmnService.predictActions(TEST_BPMN_VERSION, currentFlow);
    Assertions.assertEquals(expected.size(), actual.size());
    Assertions.assertEquals(expected, new HashSet<>(actual));
  }

  public static List<Arguments> findEventsArguments() {
    return List.of(
        Arguments.of(
            List.of(
                new SequenceActionDto("Gateway_0xhdm0s", BpmnFlowNodeType.EVENT_BASED_GATEWAY)
            ),
            Set.of(
                new ActionDto("m_inventory", "Инвентарь"),
                new ActionDto("m_dungeon", "В подземелье")
            )
        ),
        Arguments.of(
            List.of(
                new SequenceActionDto("Event_00fon4h", BpmnFlowNodeType.INTERMEDIATE_CATCH_EVENT)
            ),
            Set.of(
                new ActionDto("m_main_menu", "В меню")
            )
        )
    );
  }

}
