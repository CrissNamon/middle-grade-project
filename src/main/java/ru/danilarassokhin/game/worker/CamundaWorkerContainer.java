package ru.danilarassokhin.game.worker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.worker.JobHandler;
import io.camunda.zeebe.client.api.worker.JobWorker;
import lombok.extern.slf4j.Slf4j;
import ru.danilarassokhin.game.config.ApplicationConfig;
import tech.hiddenproject.progressive.annotation.Autofill;
import tech.hiddenproject.progressive.annotation.GameBean;
import tech.hiddenproject.progressive.basic.manager.BasicGamePublisher;

/**
 * Opens job workers in thread pool.
 */
@GameBean
@Slf4j
public class CamundaWorkerContainer {

  private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
  private final List<JobWorker> openedWorkers = new ArrayList<>();

  private final ZeebeClient zeebeClient;

  @Autofill
  public CamundaWorkerContainer(ZeebeClient zeebeClient, CamundaJobWorker... jobWorkers) {
    this.zeebeClient = zeebeClient;
    Arrays.stream(jobWorkers).forEach(this::openWorker);
    BasicGamePublisher.getInstance()
        .subscribeOn(ApplicationConfig.SHUTDOWN_EVENT, event -> openedWorkers.forEach(JobWorker::close));
  }

  public void openWorker(CamundaJobWorker jobWorker) {
    executorService.submit(() -> openWorker(jobWorker.getType(), jobWorker));
  }

  private void openWorker(String type, JobHandler jobHandler) {
    var worker = zeebeClient.newWorker()
        .jobType(type)
        .handler(jobHandler)
        .maxJobsActive(10)
        .open();
    openedWorkers.add(worker);
    log.info("Job worker opened: {}", type);
  }

}
