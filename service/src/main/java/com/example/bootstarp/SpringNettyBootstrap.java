package com.example.bootstarp;

import com.example.configuration.NettyServerProperties;
import com.example.configuration.NettySocketOptionProperties;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * 启动类
 * @author tanwenhai@bilibili.com
 */
@Component
@Slf4j
@Profile("!test")
public class SpringNettyBootstrap implements ApplicationRunner {

    @Autowired
    NettyServerProperties serverProperties;

    @Autowired
    EventLoopGroup bossGroup;

    @Autowired
    EventLoopGroup workGroup;

    @Autowired
    NettySocketOptionProperties nettySocketOptionProperties;

    @Autowired
    ConnectionInitializer connectionInitializer;

    @Override
    public void run(ApplicationArguments args) {
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workGroup)
                    .channel(serverProperties.getChannel());

            // 设置tcp三次握手的队列长度
            b.option(ChannelOption.SO_BACKLOG, nettySocketOptionProperties.getBacklog());
            if (log.isDebugEnabled()) {
                b.handler(new LoggingHandler(LogLevel.DEBUG));
            }

            // 连接处理
            b.childHandler(connectionInitializer);

            b.childOption(ChannelOption.SO_KEEPALIVE, nettySocketOptionProperties.getKeepalive());
            b.childOption(ChannelOption.AUTO_CLOSE, nettySocketOptionProperties.getAutoClose());
            b.childOption(ChannelOption.SO_REUSEADDR, nettySocketOptionProperties.getReuseaddr());
            b.childOption(ChannelOption.SO_RCVBUF, nettySocketOptionProperties.getRcvbuf());
            b.childOption(ChannelOption.SO_SNDBUF, nettySocketOptionProperties.getSndbuf());

            ChannelFuture f = b.bind(serverProperties.getAddress(), serverProperties.getPort()).sync();
            log.info("started and listening for connections on" + f.channel().localAddress());
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            throw new ApplicationContextException(e.getMessage(), e);
        }
    }
}
