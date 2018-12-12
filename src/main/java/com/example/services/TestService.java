package com.example.services;

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
public class TestService {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    Session session;

    public TextMessage consume(TextMessage msg) {
        log.debug("收到消息 {}", msg);
        session.invalidate();
        return TextMessage.newBuilder().setText(msg.getText() + " xixi").build();
    }
}
