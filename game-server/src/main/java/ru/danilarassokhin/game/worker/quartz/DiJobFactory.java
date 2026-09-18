package ru.danilarassokhin.game.worker.quartz;

import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.Scheduler;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import tech.hiddenproject.progressive.BasicComponentManager;
import tech.hiddenproject.progressive.injection.DIContainer;

/**
 * Produces job instances from DI container instead of instantiating them reflectively.
 *
 * <p>If job class is not registered as a bean, then it is created by component creator, so
 * {@link tech.hiddenproject.progressive.annotation.Autofill} constructors are still injected.
 */
@RequiredArgsConstructor
public class DiJobFactory implements JobFactory {

  private final DIContainer diContainer;

  @Override
  public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) {
    return createJob(bundle.getJobDetail().getJobClass());
  }

  private <J extends Job> J createJob(Class<J> jobClass) {
    return diContainer.searchBean(jobClass)
        .orElseGet(() -> BasicComponentManager.getComponentCreator().create(jobClass));
  }

}
