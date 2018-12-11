package com.example.session;

import com.google.common.collect.Maps;
import io.netty.util.Recycler;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

/**
 * @author tanwenhai@bilibili.com
 */
public class NettySession implements Session {

    /**
     * 存放所有session
     */
    private static ConcurrentMap<UUID, NettySession> MAP = Maps.newConcurrentMap();

    private Recycler.Handle<NettySession> handle;

    /**
     * 对象池
     */
    private static final Recycler<NettySession> RECYCLER = new Recycler<NettySession>() {
        @Override
        protected NettySession newObject(Handle<NettySession> handle) {
            return new NettySession(handle);
        }
    };

    /**
     * 创建时间
     */
    private long creationTime = Instant.now().getEpochSecond();

    /**
     * 是否是新的
     */
    private boolean isNew = false;

    /**
     * 会话id 对应map的key
     */
    private UUID sessionId;

    /**
     * 最后一次获取的时间
     */
    private long lastAccessedTime = Instant.now().getEpochSecond();

    private NettySession(Recycler.Handle<NettySession> handle) {
        this.handle = handle;
        init();
    }

    public void init() {
        sessionId = UUID.randomUUID();
        isNew = true;
        MAP.put(getId(), this);
        lastAccessedTime = Instant.now().getEpochSecond();
        creationTime = lastAccessedTime;

        MAP.put(sessionId, this);
    }

    public static NettySession valueOf(UUID sessionId) {
        NettySession session;
        if (sessionId == null) {
            session = newInstance();
        } else {
            session = MAP.get(sessionId);
        }

        if (session == null) {
            session = newInstance();
        } else {
            session.isNew = false;
            session.lastAccessedTime = Instant.now().getEpochSecond();
        }

        return session;
    }

    public static NettySession newInstance() {
        NettySession session = RECYCLER.get();
        session.init();

        return session;
    }

    @Override
    public long getCreationTime() {
        return creationTime;
    }

    @Override
    public UUID getId() {
        return sessionId;
    }

    @Override
    public long getLastAccessedTime() {
        return lastAccessedTime;
    }

    @Override
    public void setMaxInactiveInterval(int interval) {

    }

    @Override
    public int getMaxInactiveInterval() {
        return 0;
    }

    @Override
    public Object getAttribute(String name) {
        return null;
    }

    @Override
    public void setAttribute(String name, Object value) {

    }

    @Override
    public void removeAttribute(String name) {

    }

    @Override
    public void invalidate() {

    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    @Override
    public void destory() {
        MAP.remove(this.getId());
        handle.recycle(this);
    }
}
