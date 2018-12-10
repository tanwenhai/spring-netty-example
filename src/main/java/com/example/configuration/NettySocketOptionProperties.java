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

    private Integer backlog = 1 << 8;

    private Integer rcvbuf = 1 << 12;

    private Integer sndbuf = 1 << 12;

    private Boolean nodelay = true;
}
