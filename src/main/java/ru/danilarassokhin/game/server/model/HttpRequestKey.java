package ru.danilarassokhin.game.server.model;

import io.netty.handler.codec.http.HttpMethod;

/**
 * Unique identity of {@link ru.danilarassokhin.game.server.HttpRequestHandler}.
 *
 * @param method {@link HttpMethod}
 * @param contentType Content type handler consumes
 * @param uri Url handler will listen
 */
public record HttpRequestKey(HttpMethod method, String contentType, String uri) {}
