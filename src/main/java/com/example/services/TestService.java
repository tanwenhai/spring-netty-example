package com.example.services;

import com.example.annotation.MessageConsume;
import com.example.proto.TextMessage;
import com.example.session.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author tanwenhai@bilibili.com
 */
@Slf4j
@Service
public class TestService implements MessageConsume<TextMessage> {

    @Autowired
    Session session;

    @Override
    public TextMessage consume(TextMessage msg) {
        log.debug("收到消息 {}", msg);
        session.destory();
        return TextMessage.newBuilder().setText(msg.getText() + " xixi").build();
    }
}
