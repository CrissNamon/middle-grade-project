package ru.danilarassokhin.cqrs.query;

import ru.danilarassokhin.cqrs.ActionHandler;

public interface QueryHandler<I, O, T extends Query<I, O>> extends ActionHandler<I, O, T> {

}
