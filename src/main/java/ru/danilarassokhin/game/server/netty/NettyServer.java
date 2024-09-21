package ru.danilarassokhin.game.server.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import ru.danilarassokhin.game.server.impl.DefaultDispatcherController;

public class NettyServer {

  public static void start() {
    var bossGroup = new NioEventLoopGroup();
    var workerGroup = new NioEventLoopGroup();
    try {
      var serverBootstrap = new ServerBootstrap();
      serverBootstrap.group(bossGroup, workerGroup)
          .channel(NioServerSocketChannel.class)
          .handler(new LoggingHandler(LogLevel.INFO))
          .childHandler(new HttpServerInitializer(new DefaultDispatcherController()));
      var channel = serverBootstrap.bind(8080).sync().channel();
      channel.closeFuture().sync();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    } finally {
      workerGroup.shutdownGracefully();
      bossGroup.shutdownGracefully();
    }
  }
}