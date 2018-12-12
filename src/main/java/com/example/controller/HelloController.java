package com.example.controller;

import com.example.annotation.CommandController;
import com.example.annotation.CommandMapper;
import com.example.proto.TextMessage;
import com.example.services.TestService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author tanwenhai@bilibili.com
 */
@CommandController
public class HelloController {

    @Autowired
    TestService testService;

    @CommandMapper("/hello/say")
    public TextMessage say(TextMessage msg) {
        return testService.consume(msg);
    }
}
