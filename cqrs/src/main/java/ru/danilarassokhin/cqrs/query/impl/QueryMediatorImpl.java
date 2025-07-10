package ru.danilarassokhin.cqrs.query.impl;

import java.util.List;

import ru.danilarassokhin.cqrs.impl.ActionMediator;
import ru.danilarassokhin.cqrs.query.Query;
import ru.danilarassokhin.cqrs.query.QueryMediator;
import ru.danilarassokhin.cqrs.query.QueryHandler;

public class QueryMediatorImpl implements QueryMediator {

  private final ActionMediator delegate;

  public QueryMediatorImpl(List<? extends QueryHandler<?, ?, ?>> handlerList) {
    this.delegate = new ActionMediator(handlerList);
  }

  @Override
  public <I, O> O execute(Query<I, O> query) {
    return (O) delegate.execute(query);
  }

}
