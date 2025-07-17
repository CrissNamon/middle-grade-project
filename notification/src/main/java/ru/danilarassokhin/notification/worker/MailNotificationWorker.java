package ru.danilarassokhin.notification.worker;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.LongStream;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ru.danilarassokhin.cqrs.command.CommandMediator;
import ru.danilarassokhin.cqrs.query.QueryMediator;
import ru.danilarassokhin.notification.command.SendMailCommand;
import ru.danilarassokhin.notification.entity.MailNotificationEntity;
import ru.danilarassokhin.notification.query.FindOneForSendQuery;

@Component
@RequiredArgsConstructor
@Slf4j
public class MailNotificationWorker {

  private static final int THREADS_COUNT = 2;
  private static final int QUEUE_SIZE = 2;

  private final TransactionalOperator transactionalOperator;
  private final QueryMediator queryMediator;
  private final CommandMediator commandMediator;

  @Value("${mail.batch-size}")
  private int batchSize;

  private final ThreadPoolExecutor threadPoolExecutor =
      new ThreadPoolExecutor(THREADS_COUNT, THREADS_COUNT, 5, TimeUnit.MINUTES, new LinkedBlockingQueue<>(QUEUE_SIZE),
                             new ThreadPoolExecutor.CallerRunsPolicy());

  @Scheduled(fixedRateString = "${mail.timer}")
  public void processMails() {
    log.info("Starting mail processor");
    LongStream.range(0, THREADS_COUNT)
        .forEach(i -> findAndSend());
  }

  private void findAndSend() {
    threadPoolExecutor.submit(() -> {
      transactionalOperator.transactional(queryMediator.execute(new FindOneForSendQuery(batchSize))
                                              .flatMap(this::submitForSend)).subscribe();
    });
  }

  private Mono<MailNotificationEntity> submitForSend(MailNotificationEntity mailNotification) {
    return Mono.fromRunnable(() -> commandMediator.execute(new SendMailCommand(mailNotification)))
        .subscribeOn(Schedulers.boundedElastic())
        .thenReturn(mailNotification);
  }

}
