package com.example.codec;

import com.example.annotation.MessageLiteParam;
import com.example.configuration.CommandHandlerMethod;
import com.example.configuration.CommandHandlerMethodMapping;
import com.example.proto.Frame;
import com.google.common.collect.ImmutableMap;
import com.google.protobuf.ByteString;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Set;

/**
 * 分发消息
 * @author tanwenhai@bilibili.com
 */
@Component
@Slf4j
@ChannelHandler.Sharable
public class DispatchMessage extends SimpleChannelInboundHandler<Frame> implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    /**
     * protobuf 类 parseFrom方法缓存
     */
    private final static ImmutableMap<String, Method> methodCache;

    static {
        ImmutableMap.Builder<String, Method> builder = ImmutableMap.builder();
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter((metadataReader, metadataReaderFactory) -> Objects.equals(metadataReader.getClassMetadata().getSuperClassName(), "com.google.protobuf.GeneratedMessageV3"));
        Set<BeanDefinition> beanDefinitionSet = provider.findCandidateComponents("com.example.proto");
        for (BeanDefinition beanDefinition : beanDefinitionSet) {
            try {
                Class<?> clazz = Class.forName(beanDefinition.getBeanClassName());
                Method method = clazz.getMethod("parseFrom", ByteString.class);
                builder.put(clazz.getName(), method);
                log.debug("message method cache put {} -> {}#{}", clazz.getName(), clazz.getName(), method.getName());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        methodCache = builder.build();
    }

    @Autowired
    CommandHandlerMethodMapping commandHandlerMethodMapping;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Frame msg) throws Exception {
        // 将ctx绑定到业务线程
        try {
            ChannelHandlerContextHolder.setCtx(ctx);
            CommandHandlerMethod handlerMethod = commandHandlerMethodMapping.getMethod(msg.getPath());
            MethodParameter[] parameters = handlerMethod.getParameters();
            Object arg;
            if (parameters.length == 1) {
                if (parameters[0].hasParameterAnnotation(MessageLiteParam.class)) {
                    Method parseFrom = methodCache.get(parameters[0].getParameterType().getName());
                    arg = parseFrom.invoke(null, msg.getPayload());
                } else {
                    arg = msg.getPayload();
                }
                Object rtv = handlerMethod.getMethod().invoke(applicationContext.getBean(handlerMethod.getHandler()), arg);
                ctx.writeAndFlush(rtv);
            }
        } finally {
            ChannelHandlerContextHolder.clear();
        }
    }
}
