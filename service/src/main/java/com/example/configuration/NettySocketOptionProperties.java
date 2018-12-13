package com.example.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author tanwenhai@bilibili.com
 */
@ConfigurationProperties(prefix = "netty.server.socket")
@Getter
@Setter
public class NettySocketOptionProperties {
    private Boolean keepalive = true;

    /**
     * 握手队列大小
     */
    private Integer backlog = 1 << 8;

    /**
     * 接收的socket缓冲区
     */
    private Integer rcvbuf = 1 << 12;

    /**
     * 写的socket缓冲区
     */
    private Integer sndbuf = 1 << 12;

    /**
     * tcp no delay
     */
    private Boolean nodelay = true;

    private Boolean autoClose = false;

    private Boolean reuseaddr = false;

    /**
     * 等待client连接的超时时间
     */
    private Integer timeout = 6000;
}
