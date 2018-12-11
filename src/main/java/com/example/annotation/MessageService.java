package com.example.annotation;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.lang.annotation.*;

/**
 * @author tanwenhai@bilibili.com
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public @interface MessageService {
    String value() default "";
}
