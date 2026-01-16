package ru.danilarassokhin.notification.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.danilarassokhin.cqrs.command.CommandHandler;
import ru.danilarassokhin.cqrs.command.CommandMediator;
import ru.danilarassokhin.cqrs.query.QueryHandler;
import ru.danilarassokhin.cqrs.query.QueryMediator;
import ru.danilarassokhin.cqrs.query.impl.QueryMediatorImpl;
import ru.danilarassokhin.notification.service.impl.ReactiveCommandMediator;

@Configuration
public class CqrsConfig {

  @Bean
  public QueryMediator queryMediator(List<QueryHandler<?, ?, ?>> queryHandlers) {
    return new QueryMediatorImpl(queryHandlers);
  }

  @Bean
  public CommandMediator commandMediator(List<CommandHandler<?, ?, ?>> commandHandlers) {
    return new ReactiveCommandMediator(commandHandlers);
  }

}
