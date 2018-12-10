package com.example.configuration;

import io.netty.channel.EventLoopGroup;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;

/**
 * @author tanwenhai@bilibili.com
 */
@Configuration
@EnableConfigurationProperties(value = {NettySocketOptionProperties.class, NettyServerProperties.class})
public class NettyServerConfiguration {
    @Autowired
    private NettyServerProperties nettyServerProperties;

    /**
     * 只有 netty.server.ssl属性设置为true时才配置bean
     * @return
     * @throws CertificateException
     * @throws SSLException
     */
    @ConditionalOnProperty(value = "netty.server.ssl", havingValue = "true")
    @Bean
    public SslContext sslContext() throws CertificateException, SSLException {
        SelfSignedCertificate ssc = new SelfSignedCertificate();

        return SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
    }

    @Bean(destroyMethod = "shutdownGracefully")
    public EventLoopGroup bossGroup() {
        try {
            return nettyServerProperties.getBossGroup().newInstance();
        } catch (Exception e) {
            throw new ApplicationContextException("bossGroup参数配置错误", e);
        }
    }

    @Bean(destroyMethod = "shutdownGracefully")
    public EventLoopGroup workGroup() {
        try {
            return nettyServerProperties.getWorkGroup().newInstance();
        } catch (Exception e) {
            throw new ApplicationContextException("workGroup参数配置错误", e);
        }
    }

    @Bean(destroyMethod = "shutdownGracefully")
    public EventLoopGroup blockGroup() {
        try {
            return nettyServerProperties.getBlockGroup().newInstance();
        } catch (Exception e) {
            throw new ApplicationContextException("workGroup参数配置错误", e);
        }
    }
}
