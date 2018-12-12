package com.example.configuration;

import com.example.annotation.CommandController;
import com.example.annotation.CommandMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

/**
 * 获取所有bean 然后遍历获取bean CommandController注解
 * 获取CommandController注解类CommandMapper注解的方法
 *
 * @author tanwenhai@bilibili.com
 */
@Component
@Slf4j
public class CommandHandlerMethodMapping implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    private static final String SCOPED_TARGET_NAME_PREFIX = "scopedTarget.";

    private ImmutableMap<String, CommandHandlerMethod> methodCacheMap;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void initHandlerMethods() {
        String[] beanNames = applicationContext.getBeanNamesForType(Object.class);
        for (String beanName : beanNames) {
            if (!beanName.startsWith(SCOPED_TARGET_NAME_PREFIX)) {
                Class<?> beanType = null;
                try {
                    beanType = applicationContext.getType(beanName);
                } catch (Throwable ex) {
                    // An unresolvable bean type, probably from a lazy bean - let's ignore it.
                    if (log.isDebugEnabled()) {
                        log.debug("Could not resolve target class for bean with name '" + beanName + "'", ex);
                    }
                }
                if (beanType != null && isHandler(beanType)) {
                    detectHandlerMethods(beanName);
                }
            }
        }
    }

    /**
     * 处理CommandController注解的bean
     * @param handler beanName
     */
    private void detectHandlerMethods(String handler) {
        Class<?> handlerType = applicationContext.getType(handler);

        if (handlerType != null) {
            Class<?> userType = ClassUtils.getUserClass(handlerType);

            Map<Method, CommandHandlerMethod> methods = MethodIntrospector.selectMethods(userType,
                    (MethodIntrospector.MetadataLookup<CommandHandlerMethod>) method -> {
                        try {
                            return getMappingForMethod(method, handler);
                        }
                        catch (Throwable ex) {
                            throw new IllegalStateException("Invalid mapping on handler class [" +
                                    userType.getName() + "]: " + method, ex);
                        }
                    });

            if (log.isDebugEnabled()) {
                log.debug(methods.size() + " request handler methods found on " + userType + ": " + methods);
            }

            ImmutableMap.Builder<String, CommandHandlerMethod> builder = ImmutableMap.builder();

            methods.forEach((method, mapping) -> {
//                Method invocableMethod = AopUtils.selectInvocableMethod(method, userType);
                builder.put(mapping.getPath(), mapping);
            });

            methodCacheMap = builder.build();
        }
    }

    private boolean isHandler(Class<?> beanType) {
        return AnnotatedElementUtils.hasAnnotation(beanType, CommandController.class);
    }

    private CommandHandlerMethod getMappingForMethod(Method method, String handler) {
        CommandHandlerMethod info = createRequestMappingInfo(method, handler);

        return info;
    }

    private CommandHandlerMethod createRequestMappingInfo(Method method, String handler) {
        CommandMapper commandMapper = AnnotatedElementUtils.findMergedAnnotation(method, CommandMapper.class);

        Parameter[] parameters = method.getParameters();
        MethodParameter[] methodParameters = new MethodParameter[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            methodParameters[i] = MethodParameter.forParameter(parameters[i]);
        }

        return CommandHandlerMethod.builder()
                .path(commandMapper.value())
                .handler(handler)
                .method(method)
                .parameters(methodParameters)
                .build();
    }

    public CommandHandlerMethod getMethod(String path) {
        return methodCacheMap.get(path);
    }
}


