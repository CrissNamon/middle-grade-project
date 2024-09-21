package ru.danilarassokhin.game.server.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import ru.danilarassokhin.game.server.DispatcherController;
import ru.danilarassokhin.game.server.HttpRequestHandler;
import ru.danilarassokhin.game.server.impl.DefaultDispatcherController;
import ru.danilarassokhin.game.server.model.HttpRequestKey;

public class NettyServer {

  private final DispatcherController dispatcherController = new DefaultDispatcherController();

  public void get(String uri, HttpRequestHandler handler) {
    dispatcherController.addMapping(new HttpRequestKey(HttpMethod.GET, uri), handler);
  }

  public void post(String uri, HttpRequestHandler handler) {
    dispatcherController.addMapping(new HttpRequestKey(HttpMethod.POST, uri), handler);
  }

  public void start() {
    var bossGroup = new NioEventLoopGroup();
    var workerGroup = new NioEventLoopGroup();
    try {
      var serverBootstrap = new ServerBootstrap();
      serverBootstrap.group(bossGroup, workerGroup)
          .channel(NioServerSocketChannel.class)
          .handler(new LoggingHandler(LogLevel.INFO))
          .childHandler(new HttpServerInitializer(dispatcherController));
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
