package ru.danilarassokhin.game.model.camunda;

/**
 * Represents MessageEvent in Camunda.
 * @param name name of event
 * @param messageRef Message id for which this Event bounded to
 */
public record BpmnEvent(String name, String messageRef) {

}
