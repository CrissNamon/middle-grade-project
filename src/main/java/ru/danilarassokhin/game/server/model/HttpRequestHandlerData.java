package ru.danilarassokhin.game.server.model;

/**
 * Contains {@link ru.danilarassokhin.game.server.HttpRequestHandler} data.
 *
 * @param requestKey {@link HttpRequestKey}
 * @param consumes Media types handler can consume.
 * @param produces Media type handler produces
 */
public record HttpRequestHandlerData(HttpRequestKey requestKey, String consumes, String produces) {}
