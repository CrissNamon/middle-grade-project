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
import ru.danilarassokhin.notification.entity.MailNotificationEntity;
import ru.danilarassokhin.notification.repository.MailNotificationRepository;
import ru.danilarassokhin.notification.service.MailNotificationService;

@Component
@RequiredArgsConstructor
@Slf4j
public class MailNotificationWorker {

  private static final int THREADS_COUNT = 2;
  private static final int QUEUE_SIZE = 2;

  private final MailNotificationRepository mailNotificationRepository;
  private final MailNotificationService mailNotificationService;
  private final TransactionalOperator transactionalOperator;

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
      transactionalOperator.transactional(mailNotificationRepository.findForSend(batchSize)
                                              .flatMap(this::submitForSend)).subscribe();
    });
  }

  private Mono<MailNotificationEntity> submitForSend(MailNotificationEntity mailNotification) {
    return Mono.fromRunnable(() -> sendMail(mailNotification))
        .subscribeOn(Schedulers.boundedElastic())
        .thenReturn(mailNotification);
  }

  private void sendMail(MailNotificationEntity mailNotification) {
    mailNotificationService.send(mailNotification)
        .map(mailNotification::setProcessed)
        .flatMap(mailNotificationRepository::save)
        .subscribe();
  }

}
