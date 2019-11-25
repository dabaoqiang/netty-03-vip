package com.xxq.netty.vip.netty.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author xq
 * 用于对网络事件进行读写操作，通过我们只需要关注channelRead和exceptionCaught方法
 * ChannelHandlerAdapter 用于对网络事件进行读写操作
 */
public class TimeServerHandler extends ChannelHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 将msg转换成netty的ByteBuf对象，ByteBuf类似于JDK中的java.nio.ByteBuffer对象,不多它提供了
        ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        String body = new String(req, "UTF-8");
        System.out.println("thie time server receive order :" + body);
        String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new java.util.Date(System.currentTimeMillis()).toString() : "BAD ORDER";
        // pooled类型的bytebuf是在已经申请好的内存块取一块内存
        // Unpooled是直接通过JDK底层代码申请。
        ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
        ctx.write(resp);

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // 将消息发送队列中的消息写入到SocketChannel中发送给对方。
        // 从性能角度，为了防止频繁地唤醒Selector进行消息发送
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
