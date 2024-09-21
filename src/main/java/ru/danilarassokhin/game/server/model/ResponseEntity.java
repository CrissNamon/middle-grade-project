package ru.danilarassokhin.game.server.model;

import io.netty.handler.codec.http.HttpResponseStatus;

public record ResponseEntity(HttpResponseStatus status, Object body) {


}
