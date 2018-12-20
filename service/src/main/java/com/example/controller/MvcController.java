package com.example.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author tanwenhai@bilibili.com
 */
@RestController
@RequestMapping("/mvc")
public class MvcController {
    @RequestMapping("/index")
    public String index() {
        // 90.0% == 1ms
        // 95.0% == 10ms  1000 50 > 10ms
        // 99.0% == 100ms 1000 10 > 100ms
        // 99.9% == 1000ms1000 1 > 1000ms
        int level = ThreadLocalRandom.current().nextInt(1, 1000);

        int time;
        if (level <= 900) {
            time = 1;
        } else if (level <= 950) {
            time = 10;
        } else if (level <= 990) {
            time = 100;
        } else {
            time = 1000;
        }
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
        }
        return "1";
    }
}
