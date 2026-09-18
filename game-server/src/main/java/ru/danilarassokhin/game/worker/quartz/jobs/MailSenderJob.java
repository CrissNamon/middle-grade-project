package ru.danilarassokhin.game.worker.quartz.jobs;

import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import ru.danilarassokhin.game.repository.MailRepository;
import ru.danilarassokhin.game.worker.kafka.KafkaMailSender;
import ru.danilarassokhin.game.worker.quartz.QuartzJobWorker;
import ru.danilarassokhin.sql.service.TransactionManager;
import ru.danilarassokhin.util.PropertiesFactory;
import tech.hiddenproject.progressive.annotation.Autofill;
import tech.hiddenproject.progressive.annotation.GameBean;

@GameBean
@DisallowConcurrentExecution
@Slf4j
public class MailSenderJob implements QuartzJobWorker {

  public static final String JOB_GROUP = "mail";

  private static final String SENDING_DELAY_SECONDS_PROPERTY = "app.mail.sending-delay-seconds";
  private static final Integer DEFAULT_SENDING_DELAY_SECONDS = 10;
  private static final JobKey JOB_KEY = JobKey.jobKey("mailSenderJob", JOB_GROUP);
  private static final TriggerKey TRIGGER_KEY = TriggerKey.triggerKey("mailSenderTrigger", JOB_GROUP);

  private final TransactionManager transactionManager;
  private final MailRepository mailRepository;
  private final KafkaMailSender kafkaMailSender;
  private final Integer sendingDelaySeconds;

  @Autofill
  public MailSenderJob(
      TransactionManager transactionManager,
      MailRepository mailRepository,
      KafkaMailSender kafkaMailSender,
      PropertiesFactory propertiesFactory
  ) {
    this.transactionManager = transactionManager;
    this.mailRepository = mailRepository;
    this.kafkaMailSender = kafkaMailSender;
    this.sendingDelaySeconds = propertiesFactory.getAsInt(SENDING_DELAY_SECONDS_PROPERTY)
        .orElse(DEFAULT_SENDING_DELAY_SECONDS);
  }

  @Override
  public JobDetail getJobDetail() {
    return JobBuilder.newJob(MailSenderJob.class)
        .withIdentity(JOB_KEY)
        .withDescription("Sends unprocessed mail to Kafka")
        .build();
  }

  @Override
  public Trigger getTrigger() {
    return TriggerBuilder.newTrigger()
        .withIdentity(TRIGGER_KEY)
        .forJob(JOB_KEY)
        .startNow()
        .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                          .withIntervalInSeconds(sendingDelaySeconds)
                          .repeatForever()
                          .withMisfireHandlingInstructionNextWithRemainingCount())
        .build();
  }

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    log.debug("Searching for new messages");
    try {
      transactionManager.doInTransaction(ctx -> mailRepository.findOneForSend(ctx)
          .ifPresent(mailEntity -> {
            mailRepository.markProcessed(mailEntity, ctx);
            kafkaMailSender.send(mailEntity);
          }));
    } catch (RuntimeException e) {
      throw new JobExecutionException("Error while sending mail", e);
    }
  }

}
