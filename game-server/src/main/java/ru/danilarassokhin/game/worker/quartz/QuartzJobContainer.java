package ru.danilarassokhin.game.worker.quartz;

import java.util.Arrays;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import ru.danilarassokhin.game.exception.QuartzException;
import ru.danilarassokhin.server.config.WebConfig;
import tech.hiddenproject.progressive.basic.manager.BasicGamePublisher;

@Slf4j
public class QuartzJobContainer {

  private final Scheduler scheduler;

  public QuartzJobContainer(Scheduler scheduler, QuartzJobWorker... jobWorkers) {
    this.scheduler = scheduler;
    Arrays.stream(jobWorkers).forEach(this::scheduleJob);
    start();
    BasicGamePublisher.getInstance()
        .subscribeOn(WebConfig.WEB_SERVER_SHUTDOWN_EVENT_NAME, event -> shutdown());
  }

  public void scheduleJob(QuartzJobWorker jobWorker) {
    try {
      var jobDetail = jobWorker.getJobDetail();
      scheduler.scheduleJob(jobDetail, Set.of(jobWorker.getTrigger()), true);
      log.info("Quartz job scheduled: {}", jobDetail.getKey());
    } catch (SchedulerException e) {
      throw new QuartzException("Error while scheduling job " + jobWorker.getClass().getName(), e);
    }
  }

  private void start() {
    try {
      scheduler.start();
      log.info("Quartz scheduler started: {}", scheduler.getSchedulerName());
    } catch (SchedulerException e) {
      throw new QuartzException("Error while starting scheduler", e);
    }
  }

  private void shutdown() {
    try {
      scheduler.shutdown(true);
      log.info("Quartz scheduler stopped");
    } catch (SchedulerException e) {
      log.error("Error while stopping scheduler", e);
    }
  }

}
