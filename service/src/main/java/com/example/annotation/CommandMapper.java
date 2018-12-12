package com.example.annotation;

import java.lang.annotation.*;

/**
 * FIXME 使用改注解方法只能有一个参数 类型是ByteString或MessageLite的子类
 * @author tanwenhai@bilibili.com
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface CommandMapper {
    String value() default "";
}
