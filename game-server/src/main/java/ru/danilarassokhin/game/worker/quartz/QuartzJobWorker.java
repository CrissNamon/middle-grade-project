package ru.danilarassokhin.game.worker.quartz;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.Trigger;

public interface QuartzJobWorker extends Job {

  JobDetail getJobDetail();

  Trigger getTrigger();

}
