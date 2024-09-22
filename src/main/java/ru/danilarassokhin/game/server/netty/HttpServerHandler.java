package ru.danilarassokhin.game.server.netty;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import ru.danilarassokhin.game.server.DispatcherController;
import ru.danilarassokhin.game.server.model.HttpRequestKey;
import ru.danilarassokhin.game.server.model.HttpResponseEntity;
import tech.hiddenproject.aide.optional.IfTrueConditional;

public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

  private static final HttpVersion HTTP_VERSION = HttpVersion.HTTP_1_1;
  private static final int HTTP_ERROR_CODES_MIN = 400;
  private static final String HTTP_METHOD_NOT_ALLOWED_MESSAGE = "Method not allowed %s: %s";
  private static final String DEFAULT_CONTENT_TYPE = HttpHeaderValues.TEXT_PLAIN.toString();

  private final DispatcherController dispatcherController;

  public HttpServerHandler(DispatcherController dispatcherController) {
    this.dispatcherController = dispatcherController;
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) {
    var optionalRequestHandler = dispatcherController.findByKey(httpRequestToKey(msg));
    var response = optionalRequestHandler
        .map(httpRequestHandler -> httpRequestHandler.handle(msg))
        .map(responseEntity -> responseEntityToHttpResponse(responseEntity, msg))
        .orElseGet(() -> createMethodNotAllowedResponse(msg));
    var channelFeature = ctx.writeAndFlush(response);
    if (shouldCloseConnection(response)) {
      channelFeature.addListener(ChannelFutureListener.CLOSE);
    }
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) {
    ctx.flush();
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    ctx.close();
    throw new RuntimeException(cause);
  }

  private HttpRequestKey httpRequestToKey(HttpRequest httpRequest) {
    var contentType = Optional.ofNullable(httpRequest.headers().get(HttpHeaderNames.CONTENT_TYPE))
        .orElse(DEFAULT_CONTENT_TYPE);
    return new HttpRequestKey(httpRequest.method(), contentType, httpRequest.uri());
  }

  private HttpResponse responseEntityToHttpResponse(HttpResponseEntity responseEntity, HttpRequest httpRequest) {
    var responseBody = Optional.ofNullable(responseEntity.body())
        .map(body -> Unpooled.wrappedBuffer(createByteBufFromString(body)))
        .orElse(Unpooled.EMPTY_BUFFER);
    var response = new DefaultFullHttpResponse(HTTP_VERSION, responseEntity.status(), responseBody);
    response.headers().set(HttpHeaderNames.CONTENT_TYPE, responseEntity.contentType());
    response.headers().set(HttpHeaderNames.CONTENT_LENGTH, responseBody.readableBytes());
    var keepAliveValue = IfTrueConditional.create()
        .ifTrue(HttpUtil.isKeepAlive(httpRequest))
          .then(HttpHeaderValues.KEEP_ALIVE)
        .orElse(HttpHeaderValues.CLOSE);
    response.headers().set(HttpHeaderNames.CONNECTION, keepAliveValue);
    return response;
  }

  private HttpResponse createMethodNotAllowedResponse(HttpRequest httpRequest) {
    var body = String.format(HTTP_METHOD_NOT_ALLOWED_MESSAGE, httpRequest.method().name(), httpRequest.uri());
    return new DefaultFullHttpResponse(HTTP_VERSION, HttpResponseStatus.METHOD_NOT_ALLOWED, createByteBufFromString(body));
  }

  private ByteBuf createByteBufFromString(String value) {
    return Unpooled.wrappedBuffer(value.getBytes(StandardCharsets.UTF_8));
  }

  private boolean shouldCloseConnection(HttpResponse httpResponse) {
    return httpResponse.headers().containsValue(
        HttpHeaderNames.CONNECTION,
        HttpHeaderValues.CLOSE,
        true
    ) || httpResponse.status().code() >= HTTP_ERROR_CODES_MIN;
  }
}
