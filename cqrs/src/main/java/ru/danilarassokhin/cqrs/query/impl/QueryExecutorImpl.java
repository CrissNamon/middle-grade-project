package ru.danilarassokhin.cqrs.query.impl;

import java.util.List;

import ru.danilarassokhin.cqrs.impl.ActionExecutorImpl;
import ru.danilarassokhin.cqrs.query.Query;
import ru.danilarassokhin.cqrs.query.QueryExecutor;
import ru.danilarassokhin.cqrs.query.QueryHandler;

public class QueryExecutorImpl implements QueryExecutor {

  private final ActionExecutorImpl<Query<?, ?>> delegate;

  public QueryExecutorImpl(List<? extends QueryHandler<?, ?, ?>> handlerList) {
    this.delegate = new ActionExecutorImpl<>(handlerList);
  }

  @Override
  public <I, O> O execute(Query<I, O> query) {
    return (O) delegate.execute(query);
  }

}
