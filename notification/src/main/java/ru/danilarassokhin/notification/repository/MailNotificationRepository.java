package ru.danilarassokhin.notification.repository;

import java.util.UUID;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import ru.danilarassokhin.notification.entity.MailNotificationEntity;

@Repository
public interface MailNotificationRepository extends ReactiveCrudRepository<MailNotificationEntity, UUID> {

  @Query("select * from notification.mail where is_processed = false for update skip locked limit $1")
  Flux<MailNotificationEntity> findForSend(int limit);

  @Query("select * from notification.mail where is_processed = false")
  Flux<MailNotificationEntity> findAllForSend();

}
