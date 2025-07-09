package ru.danilarassokhin.cqrs;

public interface ActionExecutor<I extends Action> {

  <O> O execute(I action);

}
