package ru.danilarassokhin.notification.query;

import reactor.core.publisher.Flux;
import ru.danilarassokhin.cqrs.query.Query;
import ru.danilarassokhin.notification.entity.MailNotificationEntity;

public record FindOneForSendQuery(int limit) implements Query<Void, Flux<MailNotificationEntity>> {

}
