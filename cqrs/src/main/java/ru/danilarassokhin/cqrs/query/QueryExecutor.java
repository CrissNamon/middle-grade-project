package ru.danilarassokhin.cqrs.query;

public interface QueryExecutor {

  <I, O> O execute(Query<I, O> query);

}
