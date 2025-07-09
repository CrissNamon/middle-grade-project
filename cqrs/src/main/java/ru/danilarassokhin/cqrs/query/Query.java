package ru.danilarassokhin.cqrs.query;

import ru.danilarassokhin.cqrs.Action;

public interface Query<I, O> extends Action<I, O> {
}
