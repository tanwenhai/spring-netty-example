package com.example.configuration;

import lombok.Builder;
import lombok.Getter;
import org.springframework.core.MethodParameter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author tanwenhai@bilibili.com
 */
@Builder
@Getter
public class CommandHandlerMethod {
    private final String handler;

    private final String path;

    private final Method method;

    private final MethodParameter[] parameters;
}
