package ru.danilarassokhin.notification.query.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import ru.danilarassokhin.cqrs.query.QueryHandler;
import ru.danilarassokhin.notification.entity.MailNotificationEntity;
import ru.danilarassokhin.notification.query.FindOneForSendQuery;
import ru.danilarassokhin.notification.repository.query.MailNotificationQueryRepository;

@Component
@RequiredArgsConstructor
public class FindOneForSendQueryHandler implements QueryHandler<Integer, Flux<MailNotificationEntity>, FindOneForSendQuery> {

  private final MailNotificationQueryRepository repository;

  @Override
  public Flux<MailNotificationEntity> handle(FindOneForSendQuery query) {
    return repository.findForSend(query.limit());
  }

  @Override
  public Class<FindOneForSendQuery> getType() {
    return FindOneForSendQuery.class;
  }
}
