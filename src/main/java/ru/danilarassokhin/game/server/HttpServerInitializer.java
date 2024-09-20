package ru.danilarassokhin.game.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

  @Override
  protected void initChannel(SocketChannel channel) {
    var pipeline = channel.pipeline();
    pipeline.addLast(new HttpRequestDecoder());
    pipeline.addLast(new HttpResponseEncoder());
    pipeline.addLast(new HttpServerHandler());
  }
}
