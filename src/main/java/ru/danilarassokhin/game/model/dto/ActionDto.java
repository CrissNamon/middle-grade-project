package ru.danilarassokhin.game.model.dto;

/**
 * Represents Message events in Camunda process.
 * @param id ID of message in Camunda
 * @param name String name of message in Camunda
 */
public record ActionDto(String id, String name) {

}
