package ru.danilarassokhin.game.bpmn.util;

import static io.camunda.zeebe.protocol.Protocol.USER_TASK_JOB_TYPE;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.process.test.api.ZeebeTestEngine;
import io.camunda.zeebe.process.test.assertions.BpmnAssert;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.jupiter.api.Assertions;
import ru.danilarassokhin.game.config.CamundaConfig;
import ru.danilarassokhin.game.entity.camunda.CamundaAction;
import ru.danilarassokhin.game.util.PropertiesFactory;

@RequiredArgsConstructor
public class BpmnTestProcessInstance {

  private final ZeebeClient zeebeClient;
  private final ZeebeTestEngine zeebeTestEngine;
  private final PropertiesFactory propertiesFactory;

  private ProcessInstanceEvent processInstance;
  private String processId;

  public void init() {
    processId = propertiesFactory.getAsString(CamundaConfig.CAMUNDA_PROCESS_ID_PROPERTY).orElseThrow();
    var deployment = propertiesFactory.getAsString(CamundaConfig.CAMUNDA_DEPLOYMENTS_PROPERTY).orElseThrow();
    zeebeClient.newDeployResourceCommand()
        .addResourceFromClasspath(deployment)
        .send()
        .join();
  }

  @SafeVarargs
  public final void startProcess(ImmutablePair<String, Object>... variables) {
    processInstance = zeebeClient.newCreateInstanceCommand()
        .bpmnProcessId(processId)
        .latestVersion()
        .variables(Arrays.stream(variables).collect(
            Collectors.toMap(ImmutablePair::getLeft, ImmutablePair::getRight)))
        .send()
        .join();
  }

  @SafeVarargs
  public final void startProcessBefore(String activityId, ImmutablePair<String, Object>... variables) {
    processInstance = zeebeClient.newCreateInstanceCommand()
        .bpmnProcessId(processId)
        .latestVersion()
        .startBeforeElement(activityId)
        .variables(Arrays.stream(variables).collect(
            Collectors.toMap(ImmutablePair::getLeft, ImmutablePair::getRight)))
        .send()
        .join();
  }

  public void assertStarted() {
    BpmnAssert.assertThat(processInstance).isStarted();
  }

  public void assertWaitingAtExactly(String... activities) {
    try {
      zeebeTestEngine.waitForIdleState(Duration.ofMinutes(5));
      var records = StreamSupport.stream(BpmnAssert.getRecordStream().processInstanceRecords().spliterator(), false).toList();
      Assertions.assertTrue(records.size() >= activities.length);
      var actualActivities = records.subList(records.size() - activities.length, records.size()).stream()
          .map(record -> record.getValue().getElementId())
          .collect(Collectors.toSet());
      var expectedActivities = Arrays.stream(activities).collect(Collectors.toSet());
      Assertions.assertEquals(expectedActivities, actualActivities);
    } catch (Throwable t) {
      throw new RuntimeException(t);
    }
  }

  public void doAction(CamundaAction action) {
    executeJob(USER_TASK_JOB_TYPE, ImmutablePair.of("action", action.name()));
  }

  public void sendSignal(String signal) {
    zeebeClient.newBroadcastSignalCommand()
        .signalName(signal)
        .send()
        .join();
  }

  @SafeVarargs
  public final void executeJob(String jobType, ImmutablePair<String, Object>... variables) {
    try {
      zeebeTestEngine.waitForIdleState(Duration.ofMinutes(5));
      List<ActivatedJob> jobs = getJobs(jobType);

      ActivatedJob userTaskJob = jobs.get(0);
      zeebeClient.newCompleteCommand(userTaskJob)
          .variables(userTaskJob.getVariables())
          .variables(Arrays.stream(variables).collect(Collectors.toMap(ImmutablePair::getLeft, ImmutablePair::getRight)))
          .send()
          .join();
    } catch (Throwable t) {
      throw new RuntimeException(t);
    }
  }

  private List<ActivatedJob> getJobs(String jobType) {
    return zeebeClient
        .newActivateJobsCommand()
        .jobType(jobType)
        .maxJobsToActivate(1)
        .send()
        .join()
        .getJobs();
  }

}
