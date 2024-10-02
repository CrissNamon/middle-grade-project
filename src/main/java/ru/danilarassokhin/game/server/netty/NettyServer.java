package ru.danilarassokhin.game.server.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.danilarassokhin.game.exception.HttpServerException;

@RequiredArgsConstructor
@Slf4j
public class NettyServer {

  private final int port;
  private final ChannelHandler mainHandler;
  private final ChannelHandler childHandler;

  public void start() {
    var bossGroup = new NioEventLoopGroup();
    var workerGroup = new NioEventLoopGroup();
    try {
      var serverBootstrap = new ServerBootstrap();
      serverBootstrap.group(bossGroup, workerGroup)
          .channel(NioServerSocketChannel.class)
          .handler(mainHandler)
          .childHandler(childHandler);
      var channel = serverBootstrap.bind(port).sync().channel();
      log.info("Started server on port: {}", port);
      channel.closeFuture().sync();
    } catch (InterruptedException e) {
      throw new HttpServerException(e);
    } finally {
      workerGroup.shutdownGracefully();
      bossGroup.shutdownGracefully();
    }
  }
}
