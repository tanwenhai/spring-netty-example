package com.example.codec;

import com.example.annotation.MessageConsume;
import com.example.proto.Frame;
import com.google.common.collect.ImmutableMap;
import com.google.protobuf.MessageLite;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import javax.annotation.PostConstruct;
import java.lang.reflect.Type;

/**
 * 分发消息
 * @author tanwenhai@bilibili.com
 */
@Component
@Slf4j
@ChannelHandler.Sharable
public class DispatchMessage extends SimpleChannelInboundHandler<MessageLite> implements ApplicationContextAware {
    private ImmutableMap<String, MessageConsume> handlerCache;
    private ApplicationContext applicationContext;

    @PostConstruct
    public void init() {
        ImmutableMap.Builder<String, MessageConsume> builder = ImmutableMap.builder();
        ObjectProvider<MessageConsume> beanProvider = applicationContext.getBeanProvider(MessageConsume.class);
        for (MessageConsume consume : beanProvider) {
            Type[] genericInterfaces = consume.getClass().getGenericInterfaces();
            if (genericInterfaces.length == 1 && genericInterfaces[0] instanceof ParameterizedTypeImpl) {
                @SuppressWarnings("all")
                String msgType = ((ParameterizedTypeImpl) genericInterfaces[0]).getActualTypeArguments()[0].getTypeName();
                builder.put(msgType, consume);
            }
        }

        handlerCache = builder.build();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageLite msg) throws Exception {
        // 将ctx绑定到业务线程
        try {
            ChannelHandlerContextHolder.setCtx(ctx);
            String key = msg.getClass().getName();
            if (!(msg instanceof Frame) && handlerCache.containsKey(key)) {
                MessageConsume consume = handlerCache.get(key);

                MessageLite rtv = consume.consume(msg);
                ctx.writeAndFlush(rtv);
            } else {
                // 不处理 交给下一个处理
                ctx.write(msg);
            }
        } finally {
            ChannelHandlerContextHolder.clear();
        }
    }
}
