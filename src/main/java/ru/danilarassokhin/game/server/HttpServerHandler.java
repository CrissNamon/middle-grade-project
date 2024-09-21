package ru.danilarassokhin.game.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;

public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {

  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    ctx.flush();
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    ctx.close();
  }
}
