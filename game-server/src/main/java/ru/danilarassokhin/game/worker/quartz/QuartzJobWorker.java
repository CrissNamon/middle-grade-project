package ru.danilarassokhin.game.worker.quartz;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.Trigger;

/**
 * {@link Job} describing its own {@link JobDetail} and {@link Trigger}.
 *
 * <p>Implementations must be {@link tech.hiddenproject.progressive.annotation.GameBean}s: job
 * instances are resolved from DI container by {@link DiJobFactory} on every fire, so all
 * dependencies are injected as usual and nothing has to be put into
 * {@link org.quartz.JobDataMap}. It is important with a persistent job store, because job data map
 * is serialized into database.
 */
public interface QuartzJobWorker extends Job {

  /**
   * @return Job description to store in job store
   */
  JobDetail getJobDetail();

  /**
   * @return Trigger firing {@link #getJobDetail()}
   */
  Trigger getTrigger();

}
