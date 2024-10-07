package ru.danilarassokhin.game.server;

import static ru.danilarassokhin.game.server.netty.NettyServer.HTTP_VERSION;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.AsciiString;

/**
 * Utils for http.
 */
public class HttpUtils {

  private static final int HTTP_ERROR_CODES_MIN = 400;

  /**
   * Creates Http response with 500 response code.
   * @return {@link HttpResponse}
   */
  public static HttpResponse createInternalServerError() {
    return new DefaultFullHttpResponse(HTTP_VERSION, HttpResponseStatus.INTERNAL_SERVER_ERROR);
  }

  /**
   * Creates Http response with 401 response code.
   * @return {@link HttpResponse}
   */
  public static HttpResponse createUnauthorizedError() {
    return new DefaultFullHttpResponse(HTTP_VERSION, HttpResponseStatus.UNAUTHORIZED);
  }

  /**
   * Maps string to {@link ByteBuf}.
   *
   * @param value String value
   * @return {@link ByteBuf}
   */
  public static ByteBuf createByteBufFromString(String value) {
    return Unpooled.wrappedBuffer(value.getBytes(StandardCharsets.UTF_8));
  }

  /**
   * Checks if connection should be closed after writing.
   *
   * @param httpResponse {@link HttpResponse}
   * @return true if connection should be closed
   */
  public static boolean shouldCloseConnection(HttpResponse httpResponse) {
    return httpResponse.headers().containsValue(
        HttpHeaderNames.CONNECTION,
        HttpHeaderValues.CLOSE,
        true
    ) || httpResponse.status().code() >= HTTP_ERROR_CODES_MIN;
  }

  /**
   * Gets header value from {@link HttpRequest}.
   * @param httpRequest {@link HttpRequest}
   * @param name Header name
   * @return Optional header value
   */
  public static Optional<String> getHeaderValue(HttpRequest httpRequest, String name) {
    return Optional.ofNullable(httpRequest.headers().get(name));
  }

  /**
   * Gets header value from {@link HttpRequest}.
   * @param httpRequest {@link HttpRequest}
   * @param name Header name
   * @return Optional header value
   */
  public static Optional<String> getHeaderValue(HttpRequest httpRequest, AsciiString name) {
    return Optional.ofNullable(httpRequest.headers().get(name));
  }

}
