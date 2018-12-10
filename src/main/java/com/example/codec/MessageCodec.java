package com.example.codec;

import com.example.proto.Frame;
import com.google.common.collect.ImmutableMap;
import com.google.protobuf.ByteString;
import com.google.protobuf.MessageLite;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author tanwenhai@bilibili.com
 */
@Component
@Slf4j
@ChannelHandler.Sharable
public class MessageCodec extends MessageToMessageCodec<Frame, MessageLite> {

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

    @Override
    protected void encode(ChannelHandlerContext ctx, MessageLite msg, List<Object> out) throws Exception {
        Frame frame = Frame.newBuilder()
                .setMessageType(msg.getClass().getName())
                .setPayload(msg.toByteString())
                .build();
        out.add(frame);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, Frame msg, List<Object> out) throws Exception {
        String type = msg.getMessageType();
        ByteString body = msg.getPayload();

        Method method = methodCache.get(type);
        if (method == null) {
            // TODO 返回一个错误消息
        } else {
            out.add(method.invoke(null, body));
        }
    }
}
