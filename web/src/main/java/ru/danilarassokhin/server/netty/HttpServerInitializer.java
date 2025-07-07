package ru.danilarassokhin.server.netty;

import java.util.Set;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.RequiredArgsConstructor;
import ru.danilarassokhin.server.DispatcherController;
import ru.danilarassokhin.server.HttpRequestFilter;

/**
 * Initializer for Netty's pipeline.
 * Uses different {@link EventExecutorGroup} for {@link HttpFilterHandler} and {@link HttpServerHandler}.
 */
@RequiredArgsConstructor
public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

  private static final int DEFAULT_HTTP_MAX_CONTENT_LENGTH = 1048576;
  private static final int BUSINESS_LOGIC_EXECUTOR_THREADS = 16;

  private final DispatcherController dispatcherController;
  private final EventExecutorGroup businessLogicExecutorGroup =
      new DefaultEventExecutorGroup(BUSINESS_LOGIC_EXECUTOR_THREADS);
  private final Set<HttpRequestFilter> requestFilterSet;

  @Override
  protected void initChannel(SocketChannel channel) {
    channel.pipeline().addLast(
        new HttpServerCodec(),
        new HttpObjectAggregator(DEFAULT_HTTP_MAX_CONTENT_LENGTH)
    );
    channel.pipeline().addLast(
        businessLogicExecutorGroup,
        new HttpFilterHandler(requestFilterSet),
        new HttpServerHandler(dispatcherController)
    );
  }
}
