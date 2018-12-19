package com.example.codec;

import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

import static io.netty.channel.ChannelFutureListener.CLOSE_ON_FAILURE;

/**
 * @author tanwenhai@bilibili.com
 */
@ChannelHandler.Sharable
public class HeartbeatServerHandler extends ChannelInboundHandlerAdapter {
    private static final ByteBuf HEARTBEAT_PACK = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("xxx", Charsets.UTF_8));

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            ctx.writeAndFlush(HEARTBEAT_PACK.duplicate()).addListener(CLOSE_ON_FAILURE);
        }
    }
}
