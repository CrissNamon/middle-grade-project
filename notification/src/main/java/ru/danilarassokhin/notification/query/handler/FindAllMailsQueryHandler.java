package ru.danilarassokhin.notification.query.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import ru.danilarassokhin.cqrs.query.QueryHandler;
import ru.danilarassokhin.notification.entity.MailNotificationEntity;
import ru.danilarassokhin.notification.query.FindAllMailsQuery;
import ru.danilarassokhin.notification.repository.query.MailNotificationQueryRepository;

@RequiredArgsConstructor
@Component
public class FindAllMailsQueryHandler implements QueryHandler<Void, Flux<MailNotificationEntity>, FindAllMailsQuery> {

  private final MailNotificationQueryRepository mailNotificationQueryRepository;

  @Override
  public Flux<MailNotificationEntity> handle(FindAllMailsQuery action) {
    return mailNotificationQueryRepository.findAll();
  }

  @Override
  public Class<FindAllMailsQuery> getType() {
    return FindAllMailsQuery.class;
  }
}
