package ru.danilarassokhin.game.server.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.RequiredArgsConstructor;
import ru.danilarassokhin.game.server.DispatcherController;

@RequiredArgsConstructor
public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

  private static final int DEFAULT_HTTP_MAX_CONTENT_LENGTH = 1048576;
  private static final int BUSINESS_LOGIC_EXECUTOR_THREADS = 16;

  private final DispatcherController dispatcherController;
  private final EventExecutorGroup businessLogicExecutorGroup =
      new DefaultEventExecutorGroup(BUSINESS_LOGIC_EXECUTOR_THREADS);

  @Override
  protected void initChannel(SocketChannel channel) {
    channel.pipeline().addLast(
        new HttpServerCodec(),
        new HttpObjectAggregator(DEFAULT_HTTP_MAX_CONTENT_LENGTH)
    );
    channel.pipeline().addLast(
        businessLogicExecutorGroup,
        new HttpServerHandler(dispatcherController)
    );
  }
}
