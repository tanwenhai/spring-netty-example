package com.example.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.FastThreadLocal;

/**
 * @author tanwenhai@bilibili.com
 */
public class ChannelHandlerContextHolder {
    private static final FastThreadLocal<ChannelHandlerContext> threadLocal = new FastThreadLocal<>();

    static void setCtx(ChannelHandlerContext ctx) {
        threadLocal.set(ctx);
    }

    public static ChannelHandlerContext getCtx() {
        return threadLocal.get();
    }

    public static void clear() {
        threadLocal.remove();
    }
}
