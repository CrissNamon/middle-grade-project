package ru.danilarassokhin.game.server.model;

public record HttpRequestHandlerData(HttpRequestKey requestKey, String consumes, String produces) {}
