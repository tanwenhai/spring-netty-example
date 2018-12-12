package com.example.configuration;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Constructor;
import java.util.concurrent.ThreadFactory;

/**
 * @author tanwenhai@bilibili.com
 */
@Getter
@Setter
public class EventLoopGroupProperties {
    /**
     * @see #newInstance
     */
    private Class<? extends EventLoopGroup> eventLoopGroup = NioEventLoopGroup.class;

    /**
     * 线程数
     */
    private int threads = 0;

    private String threadPrefix;

    public EventLoopGroupProperties(String threadPrefix) {
        this.threadPrefix = threadPrefix;
    }

    public EventLoopGroup newInstance() throws Exception {
        if (threadPrefix != null) {
            ThreadFactory threadFactory = new DefaultThreadFactory(threadPrefix);

            Constructor<? extends EventLoopGroup> constructor = eventLoopGroup.getConstructor(int.class, ThreadFactory.class);
            return constructor.newInstance(threads, threadFactory);
        }

        Constructor<? extends EventLoopGroup> constructor = eventLoopGroup.getConstructor(int.class);

        return constructor.newInstance(threads);
    }
}
