package ru.danilarassokhin.notification.repository.command;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import ru.danilarassokhin.notification.entity.MailNotificationEntity;

@Repository
public interface MailNotificationCommandRepository extends ReactiveCrudRepository<MailNotificationEntity, UUID> {

}
