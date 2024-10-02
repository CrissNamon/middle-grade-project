package ru.danilarassokhin.game.server.netty;

import static ru.danilarassokhin.game.server.HttpUtils.createInternalServerError;

import java.util.Set;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;
import ru.danilarassokhin.game.exception.AuthenticationException;
import ru.danilarassokhin.game.server.HttpRequestFilter;
import ru.danilarassokhin.game.server.HttpUtils;

/**
 * Handler for filtering http requests.
 * @see HttpRequestFilter
 */
@Slf4j
@Sharable
public class HttpFilterHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

  private final Set<HttpRequestFilter> filterChain;

  public HttpFilterHandler(Set<HttpRequestFilter> filters) {
    this.filterChain = filters;
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) {
    try {
      filterChain.forEach(httpRequestFilter -> httpRequestFilter.filter(msg));
      msg.retain();
      ctx.fireChannelRead(msg);
    } catch (AuthenticationException e) {
      ctx.writeAndFlush(HttpUtils.createUnauthorizedError())
          .addListener(ChannelFutureListener.CLOSE);
    }
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    log.error("Exception during channel read", cause);
    ctx.writeAndFlush(createInternalServerError())
        .addListener(ChannelFutureListener.CLOSE);
  }
}
