package ru.danilarassokhin.game.bpmn.util;

import static io.camunda.zeebe.protocol.Protocol.USER_TASK_JOB_TYPE;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.process.test.api.ZeebeTestEngine;
import io.camunda.zeebe.process.test.assertions.BpmnAssert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import ru.danilarassokhin.game.config.CamundaConfig;
import ru.danilarassokhin.game.entity.camunda.CamundaAction;
import ru.danilarassokhin.game.util.PropertiesFactory;
import ru.danilarassokhin.game.worker.CamundaJobWorker;
import ru.danilarassokhin.game.worker.CamundaWorkerContainer;

@Slf4j
public class BpmnTestProcessInstance {

  private final Map<Class<? extends CamundaJobWorker>, CamundaJobWorker> mockedWorkers = new HashMap<>();

  private final ZeebeClient zeebeClient;
  private final ZeebeTestEngine zeebeTestEngine;
  private final PropertiesFactory propertiesFactory;
  private final CamundaWorkerContainer workerContainer;

  private ProcessInstanceEvent processInstance;
  private String processId;

  public BpmnTestProcessInstance(ZeebeClient zeebeClient, ZeebeTestEngine zeebeTestEngine,
                                 PropertiesFactory propertiesFactory) {
    this.zeebeClient = zeebeClient;
    this.zeebeTestEngine = zeebeTestEngine;
    this.propertiesFactory = propertiesFactory;
    this.workerContainer = new CamundaWorkerContainer(zeebeClient);
  }

  public void init() {
    processId = propertiesFactory.getAsString(CamundaConfig.CAMUNDA_PROCESS_ID_PROPERTY).orElseThrow();
    var deployment = propertiesFactory.getAsString(CamundaConfig.CAMUNDA_DEPLOYMENTS_PROPERTY).orElseThrow();
    zeebeClient.newDeployResourceCommand()
        .addResourceFromClasspath(deployment)
        .send()
        .join();
  }

  public void mockWorker(Class<? extends CamundaJobWorker> workerClass) {
    try {
      var mockedWorker = Mockito.mock(workerClass);
      mockedWorkers.put(workerClass, mockedWorker);
      Mockito.doAnswer(invocationOnMock -> {
        var jobClient = invocationOnMock.getArgument(0, JobClient.class);
        var activatedJob = invocationOnMock.getArgument(1, ActivatedJob.class);
        jobClient.newCompleteCommand(activatedJob).send().join();
        log.info("Mocked worker {} handled job", activatedJob.getType());
        return null;
      }).when(mockedWorker).handle(Mockito.any(), Mockito.any());
      Mockito.doCallRealMethod().when(mockedWorker).getType();
      workerContainer.openWorker(mockedWorker);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public void assertExecuted(Class<? extends CamundaJobWorker> workerClass) {
    try {
      zeebeTestEngine.waitForIdleState(Duration.ofMinutes(5));
      var worker = mockedWorkers.get(workerClass);
      Mockito.verify(worker).handle(Mockito.any(), Mockito.any());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
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
      var recordStreamSpliterator = BpmnAssert.getRecordStream().processInstanceRecords().spliterator();
      var records = StreamSupport.stream(recordStreamSpliterator, false).toList();
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

      ActivatedJob userTaskJob = jobs.getFirst();
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
