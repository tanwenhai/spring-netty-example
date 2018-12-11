package com.example.session;

import java.util.UUID;

/**
 * @author tanwenhai@bilibili.com
 */
public interface Session {
    /**
     * 获取会话创建时间
     * @return
     */
    long getCreationTime();


    /**
     * 获取sessionId
     * @return
     */
    UUID getId();


    /**
     * 获取最后一次访问时间
     * @return
     */
    long getLastAccessedTime();

    /**
     * 获取最大空闲时间
     * @param interval
     */
    void setMaxInactiveInterval(int interval);


    /**
     * 设置最大空闲时间
     * @return
     */
    int getMaxInactiveInterval();

    /**
     * 获取属性
     * @param name
     * @return
     */
    Object getAttribute(String name);

    /**
     * 设置属性
     * @param name
     * @param value
     */
    void setAttribute(String name, Object value);

    /**
     * 移除属性
     * @param name
     */
    void removeAttribute(String name);

    /**
     * 销毁
     */
    void invalidate();

    /**
     * 是否是新的
     * @return
     */
    boolean isNew();
}
