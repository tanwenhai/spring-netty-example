package com.example.annotation;

import java.lang.annotation.*;

/**
 * @author tanwenhai@bilibili.com
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface CommandMapper {
    String value() default "";
}
