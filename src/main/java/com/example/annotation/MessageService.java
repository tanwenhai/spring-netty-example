package com.example.annotation;

import java.lang.annotation.*;

/**
 * @author tanwenhai@bilibili.com
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MessageService {
    String value() default "";
}
