package ru.danilarassokhin.game.server.model;

import io.netty.handler.codec.http.HttpResponseStatus;

public record HttpResponseEntity(String contentType, String body, HttpResponseStatus status) {}
