package ru.danilarassokhin.game.server.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import ru.danilarassokhin.game.server.DispatcherController;

public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

  private static final int DEFAULT_HTTP_MAX_CONTENT_LENGTH = 1048576;

  private final DispatcherController dispatcherController;

  public HttpServerInitializer(DispatcherController dispatcherController) {
    this.dispatcherController = dispatcherController;
  }

  @Override
  protected void initChannel(SocketChannel channel) {
    channel.pipeline().addLast(
        new HttpServerCodec(),
        new HttpObjectAggregator(DEFAULT_HTTP_MAX_CONTENT_LENGTH),
        new HttpServerHandler(dispatcherController)
    );
  }
}
