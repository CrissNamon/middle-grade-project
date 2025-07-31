package ru.danilarassokhin.game.worker.camunda;

import io.camunda.zeebe.client.api.worker.JobHandler;

/**
 * {@link JobHandler} returning its job type.
 */
public interface CamundaJobWorker extends JobHandler {

  /**
   * @return Job type
   */
  String getType();

}
