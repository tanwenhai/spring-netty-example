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
    /**
     * 服务器端口
     */
    private Integer port = 9999;

    /**
     * 服务器IP
     */
    private InetAddress address = InetAddress.getLoopbackAddress();

    /**
     * 开启 SSL
     */
    private boolean ssl = false;

    /**
     * 处理业务的线程组
     */
    private EventLoopGroupProperties blockGroup = new EventLoopGroupProperties("business");

    /**
     * 处理连接的线程组
     */
    private EventLoopGroupProperties bossGroup = new EventLoopGroupProperties("boss");

    /**
     * 处理读写的线程组
     */
    private EventLoopGroupProperties workGroup = new EventLoopGroupProperties("work");

    /**
     * channel 类型 windows nio linux epool
     */
    private Class<? extends ServerChannel> channel = NioServerSocketChannel.class;
}
