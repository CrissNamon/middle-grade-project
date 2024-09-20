package ru.danilarassokhin.game.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;

public class HttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
    switch (msg) {
      case LastHttpContent lastHttpContent -> {}
      case HttpRequest httpRequest -> {}
      default -> throw new RuntimeException("Unknown HttpObject type");
    }
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    ctx.flush();
  }
}
