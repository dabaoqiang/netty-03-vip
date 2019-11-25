package com.xxq.netty.vip.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class TimeClient {
    public void connect(int port, String host) {
        // 配置客户端NIO线程组
        NioEventLoopGroup group = new NioEventLoopGroup();
        // 客户端辅助启动类Bootstrap
        Bootstrap b = new Bootstrap();
        try {
            b.group(group).channel(NioSocketChannel.class).
                    option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        // 当创建NioSocketChannel成功之后，在初始化它的时候将它的channelHandler设置到ChannelPipeline
                        // 用于处理网络I/O事件
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new TimeClientHandler());
                        }
                    });
            // 发起异步连接操作
            // 客户端启动辅助类设置完成之后，调用connect方法发起异步连接，然后调用同步方法等待连接成功
            ChannelFuture f = b.connect(host, port).sync();
            //  等待客户端链路关闭
            // 最后当客户端连接关闭之后，客户端主函数退出，在退出之前，释放NIO线程组资源
            f.channel().closeFuture().sync();

        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            // 优雅退出，释放NIO线程组
            group.shutdownGracefully();
        }

    }

    public static void main(String[] args) {
        int port = 8080;
        new TimeClient().connect(port, "127.0.0.1");

    }


}


