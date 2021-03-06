package com.example.bootstarp;

import com.example.codec.DispatchMessage;
import com.example.codec.HeartbeatServerHandler;
import com.example.proto.Frame;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 新连接
 * @author tanwenhai@bilibili.com
 */
@Component
@Slf4j
public class ConnectionInitializer extends ChannelInitializer<SocketChannel> {

    @Autowired(required = false)
    SslContext sslCtx;

    @Autowired
    DispatchMessage dispatchMessage;

    @Autowired
    EventLoopGroup blockGroup;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        if (sslCtx != null) {
            // 开启ssl
            p.addFirst(sslCtx.newHandler(ch.alloc()));
        }

        if (log.isDebugEnabled()) {
            p.addLast(new LoggingHandler(LogLevel.DEBUG));
        }

        p.addLast(new ProtobufVarint32FrameDecoder())
                .addLast(new ProtobufDecoder(Frame.getDefaultInstance()))
                .addLast(new ProtobufVarint32LengthFieldPrepender())
                .addLast(new ProtobufEncoder())
//                .addLast(new IdleStateHandler(4, 4, 7, TimeUnit.SECONDS))
//                .addLast(new HeartbeatServerHandler())
                .addLast(blockGroup, dispatchMessage);
    }
}
