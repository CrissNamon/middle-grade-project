package ru.danilarassokhin.game.server;

import io.netty.handler.codec.http.FullHttpRequest;
import ru.danilarassokhin.game.server.model.ResponseEntity;

public interface HttpRequestHandler {

  ResponseEntity handle(FullHttpRequest httpRequest);

}
