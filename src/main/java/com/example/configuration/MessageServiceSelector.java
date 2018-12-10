package com.example.configuration;

import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.core.Ordered;
import org.springframework.core.type.AnnotationMetadata;

/**
 * 注册标记了@MessageService注解的类到ico容器
 *
 * @author tanwenhai@bilibili.com
 */
public class MessageServiceSelector implements DeferredImportSelector, Ordered {

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{MessageServiceAutoConfiguration.class.getName()};
    }

    @Override
    public int getOrder() {
        return 1000;
    }
}
