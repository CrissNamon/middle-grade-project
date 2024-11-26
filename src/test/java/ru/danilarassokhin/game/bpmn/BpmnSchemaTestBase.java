package ru.danilarassokhin.game.bpmn;

import java.util.function.Consumer;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.process.test.api.ZeebeTestEngine;
import io.camunda.zeebe.process.test.extension.ZeebeProcessTest;
import org.junit.jupiter.api.BeforeEach;
import ru.danilarassokhin.game.bpmn.util.BpmnTestProcessInstance;
import ru.danilarassokhin.game.util.PropertiesFactory;
import ru.danilarassokhin.game.util.impl.PropertiesFactoryImpl;

@ZeebeProcessTest
abstract class BpmnSchemaTestBase {

  private final PropertiesFactory propertiesFactory = new PropertiesFactoryImpl();

  private ZeebeClient zeebeClient;
  private ZeebeTestEngine zeebeTestEngine;
  private BpmnTestProcessInstance testProcessInstance;

  @BeforeEach
  public void init() {
    testProcessInstance = new BpmnTestProcessInstance(zeebeClient, zeebeTestEngine, propertiesFactory);
    testProcessInstance.init();
  }

  protected void bpmnProcess(Consumer<BpmnTestProcessInstance> processAssertions) {
    processAssertions.accept(testProcessInstance);
  }

}
