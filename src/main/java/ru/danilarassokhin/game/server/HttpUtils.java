package ru.danilarassokhin.game.server;

import static ru.danilarassokhin.game.server.netty.NettyServer.HTTP_VERSION;

import java.nio.charset.StandardCharsets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

public class HttpUtils {

  private static final int HTTP_ERROR_CODES_MIN = 400;

  public static HttpResponse createInternalServerError() {
    return new DefaultFullHttpResponse(HTTP_VERSION, HttpResponseStatus.INTERNAL_SERVER_ERROR);
  }

  public static HttpResponse createUnauthorizedError() {
    return new DefaultFullHttpResponse(HTTP_VERSION, HttpResponseStatus.UNAUTHORIZED);
  }

  public static ByteBuf createByteBufFromString(String value) {
    return Unpooled.wrappedBuffer(value.getBytes(StandardCharsets.UTF_8));
  }

  public static boolean shouldCloseConnection(HttpResponse httpResponse) {
    return httpResponse.headers().containsValue(
        HttpHeaderNames.CONNECTION,
        HttpHeaderValues.CLOSE,
        true
    ) || httpResponse.status().code() >= HTTP_ERROR_CODES_MIN;
  }

}
