package ru.danilarassokhin.game.server.model;

import io.netty.handler.codec.http.HttpMethod;

public record HttpRequestKey(HttpMethod method, String contentType, String uri) {}
