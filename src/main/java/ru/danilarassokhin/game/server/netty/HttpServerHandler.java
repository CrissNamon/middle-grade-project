package ru.danilarassokhin.game.server.netty;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpMessage;
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
import ru.danilarassokhin.game.server.model.ResponseEntity;
import tech.hiddenproject.aide.optional.IfTrueConditional;

public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpMessage> {

  private static final HttpVersion HTTP_VERSION = HttpVersion.HTTP_1_1;
  private static final HttpResponse METHOD_NOT_ALLOWED_RESPONSE =
      new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.METHOD_NOT_ALLOWED);

  private final DispatcherController dispatcherController;

  public HttpServerHandler(DispatcherController dispatcherController) {
    this.dispatcherController = dispatcherController;
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, FullHttpMessage httpObject) {
    var msg = (FullHttpRequest) httpObject;
    var optionalRequestHandler = dispatcherController.findByKey(httpRequestToKey(msg));
    var response = optionalRequestHandler
        .map(httpRequestHandler -> httpRequestHandler.handle(msg))
            .map(responseEntity -> responseEntityToHttpResponse(responseEntity, msg))
                .orElse(METHOD_NOT_ALLOWED_RESPONSE);
    var channelFeature = ctx.write(response);
    if (response.headers().containsValue(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE, true)) {
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
    return new HttpRequestKey(httpRequest.method(), httpRequest.uri());
  }

  private HttpResponse responseEntityToHttpResponse(ResponseEntity responseEntity, HttpRequest httpRequest) {
    var responseBody = Optional.ofNullable(responseEntity.body())
        .map(body -> Unpooled.wrappedBuffer(body.toString().getBytes(StandardCharsets.UTF_8)))
        .orElse(Unpooled.EMPTY_BUFFER);
    var response = new DefaultFullHttpResponse(HTTP_VERSION, responseEntity.status(), responseBody);
    response.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN);
    response.headers().set(HttpHeaderNames.CONTENT_LENGTH, responseBody.readableBytes());
    var keepAliveValue = IfTrueConditional.create()
        .ifTrue(HttpUtil.isKeepAlive(httpRequest))
          .then(HttpHeaderValues.KEEP_ALIVE)
        .orElse(HttpHeaderValues.CLOSE);
    response.headers().set(HttpHeaderNames.CONNECTION, keepAliveValue);
    return response;
  }
}
