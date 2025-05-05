package ru.danilarassokhin.server.model;

import ru.danilarassokhin.server.HttpRequestHandler;

/**
 * Contains {@link HttpRequestHandler} data.
 *
 * @param requestKey {@link HttpRequestKey}
 * @param consumes Media types handler can consume.
 * @param produces Media type handler produces
 */
public record HttpRequestHandlerData(HttpRequestKey requestKey, String consumes, String produces) {}
