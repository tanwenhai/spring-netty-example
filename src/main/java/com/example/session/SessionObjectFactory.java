package com.example.session;

import com.example.codec.ChannelHandlerContextHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectFactory;

import java.io.Serializable;
import java.util.UUID;

/**
 * @author tanwenhai@bilibili.com
 */
@Slf4j
public class SessionObjectFactory implements ObjectFactory<Session>, Serializable {

    @Override
    public Session getObject() {
        ChannelHandlerContext ctx = ChannelHandlerContextHolder.getCtx();
        // 获取sessionId
        Attribute<UUID> sessionId = ctx.channel().attr(AttributeKey.valueOf(String.class, "sessionId"));
        Session session = NettySession.valueOf(sessionId.get());
        if (sessionId.get() == null) {
            sessionId.set(session.getId());
        }
        return session;
    }
}
