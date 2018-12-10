package com.example.annotation;

import com.google.protobuf.MessageLite;

/**
 * 消费一个消息
 * @author tanwenhai@bilibili.com
 */
public interface MessageConsume<T extends MessageLite> {
    MessageLite consume(T msg);
}
