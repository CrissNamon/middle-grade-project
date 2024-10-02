package ru.danilarassokhin.game.server.netty;

import static ru.danilarassokhin.game.server.HttpUtils.createByteBufFromString;
import static ru.danilarassokhin.game.server.HttpUtils.createInternalServerError;
import static ru.danilarassokhin.game.server.HttpUtils.shouldCloseConnection;
import static ru.danilarassokhin.game.server.netty.NettyServer.HTTP_VERSION;

import java.util.Optional;

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
import io.netty.handler.codec.http.HttpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.danilarassokhin.game.server.DispatcherController;
import ru.danilarassokhin.game.exception.HttpServerException;
import ru.danilarassokhin.game.server.model.HttpResponseEntity;
import tech.hiddenproject.aide.optional.IfTrueConditional;

/**
 * Handler for processing http requests.
 * Uses {@link DispatcherController} for dispatching requests among handlers.
 * @see DispatcherController
 */
@RequiredArgsConstructor
@Slf4j
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

  private final DispatcherController dispatcherController;

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) {
    try {
      var response = responseEntityToHttpResponse(dispatcherController.handleRequest(msg), msg);
      var channelFeature = ctx.writeAndFlush(response);
      if (shouldCloseConnection(response)) {
        channelFeature.addListener(ChannelFutureListener.CLOSE);
      }
    } catch (Throwable t) {
      ctx.writeAndFlush(createInternalServerError())
          .addListener(ChannelFutureListener.CLOSE);
      throw new HttpServerException(t);
    }
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) {
    ctx.flush();
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    log.error("Exception during channel read", cause);
    ctx.writeAndFlush(createInternalServerError())
        .addListener(ChannelFutureListener.CLOSE);
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
}
