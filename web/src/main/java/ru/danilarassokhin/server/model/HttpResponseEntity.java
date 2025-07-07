package ru.danilarassokhin.server.model;

import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * Model for http response.
 *
 * @param contentType Media type of response
 * @param body Response body
 * @param status Response HTTP status
 */
public record HttpResponseEntity(String contentType, String body, HttpResponseStatus status) {}
