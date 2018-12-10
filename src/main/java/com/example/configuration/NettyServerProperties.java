package com.example.configuration;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.InetAddress;

/**
 * @author tanwenhai@bilibili.com
 */
@ConfigurationProperties(prefix = "netty.server")
@Getter
@Setter
public class NettyServerProperties {
    private Integer port = 9999;

    private InetAddress address = InetAddress.getLoopbackAddress();

    private boolean ssl = false;

    private Class<? extends EventLoopGroup> bossGroup = NioEventLoopGroup.class;

    private Class<? extends EventLoopGroup> workGroup = NioEventLoopGroup.class;

    private Class<? extends ServerChannel> channel = NioServerSocketChannel.class;
}
