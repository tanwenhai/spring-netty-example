package com.example.annotation;

import java.lang.annotation.*;

/**
 * @author tanwenhai@bilibili.com
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MessageLiteParam {
    boolean required() default true;
}
