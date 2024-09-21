package ru.danilarassokhin.game.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpRequestEncoder;

public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

  @Override
  protected void initChannel(SocketChannel channel) {
    channel.pipeline().addLast(
        new HttpRequestDecoder(),
        new HttpObjectAggregator(1048576),
        new HttpRequestEncoder(),
        new HttpServerHandler()
    );
  }
}
