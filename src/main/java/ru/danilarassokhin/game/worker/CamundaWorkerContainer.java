package ru.danilarassokhin.game.worker;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.worker.JobHandler;
import lombok.extern.slf4j.Slf4j;
import tech.hiddenproject.progressive.annotation.Autofill;
import tech.hiddenproject.progressive.annotation.GameBean;

/**
 * Opens job workers in thread pool.
 */
@GameBean
@Slf4j
public class CamundaWorkerContainer {

  private final ZeebeClient zeebeClient;
  private final ExecutorService executorService;

  @Autofill
  public CamundaWorkerContainer(ZeebeClient zeebeClient, CamundaJobWorker... jobWorkers) {
    this.zeebeClient = zeebeClient;
    this.executorService = Executors.newFixedThreadPool(jobWorkers.length);
    Arrays.stream(jobWorkers).forEach(this::openInThreadTask);
  }

  private void openInThreadTask(CamundaJobWorker jobWorker) {
    executorService.submit(() -> openWorker(jobWorker.getType(), jobWorker));
  }

  private void openWorker(String type, JobHandler jobHandler) {
    try (var worker = zeebeClient.newWorker()
             .jobType(type)
             .handler(jobHandler)
             .maxJobsActive(10)
             .open()) {
      log.info("Job worker opened: {}", type);
    }
  }

}
