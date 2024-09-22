package ru.danilarassokhin.game.server;

import io.netty.handler.codec.http.FullHttpRequest;
import ru.danilarassokhin.game.server.model.HttpResponseEntity;

public interface HttpRequestHandler {

  HttpResponseEntity handle(FullHttpRequest httpRequest);

}
