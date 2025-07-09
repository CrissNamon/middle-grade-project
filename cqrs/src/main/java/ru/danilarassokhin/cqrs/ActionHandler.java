package ru.danilarassokhin.cqrs;

public interface ActionHandler<I, O, A extends Action<I, O>> {

  O handle(A action);

  Class<A> getType();

}
