package com.example.services;

import com.example.annotation.MessageConsume;
import com.example.proto.TextMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author tanwenhai@bilibili.com
 */
@Service
@Slf4j
public class TestService implements MessageConsume<TextMessage> {

    @Override
    public TextMessage consume(TextMessage msg) {
        log.debug("收到消息 {}", msg);
        return TextMessage.newBuilder().setText(msg.getText() + " xixi").build();
    }
}
