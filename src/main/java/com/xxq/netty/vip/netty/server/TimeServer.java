package com.xxq.netty.vip.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class TimeServer {

    public void bind(int port) {
        // Reactor线程组
        // 用于服务器端接受客户端的连接
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        // 用于进行SocketChannel的网络读写
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
        // Netty启动NIO服务端的辅助启动类，目的是降低服务端的开发复杂度
        ServerBootstrap b = new ServerBootstrap();
        // 将两个NIO线程组当作入参传递到ServerBootstrap中。
        b.group(bossGroup, workGroup)
                // NIO NioServerSocketChannel eg ServerSocket
                // 对应于JDK NIO 类库中的ServerSocketChannel类
                .channel(NioServerSocketChannel.class)
                // 配置NioServerSocketChannel 的TCP参数，将它的backlog设置为1024
                .option(ChannelOption.SO_BACKLOG, 1024)
                // 处理网络I/O事件，例如记录日志，对消息进行编解码
                // 作用类似于Reactor模式中的handler类，主要用来处理网络I/O事件，例如，记录日志，对消息进行编解码等
                .childHandler(new ChildChannelHandler());
        try {
            // bind方法绑定监听端口,然后调用它的同步阻塞方法sync等待绑定操作完成
            // 完成之后，返回一个ChannelFuture，类似于JDK中的Future，主要用于异步操作的通知回调
            ChannelFuture f = b.bind(port).sync();
            // 等待服务端链路关闭之后，main函数才推出
            f.channel().closeFuture().sync();


        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 释放相关联的资源
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }


    }

    public static void main(String[] args) {
        int port = 8080;
        new TimeServer().bind(port);
    }

}
