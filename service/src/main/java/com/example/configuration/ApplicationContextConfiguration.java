package com.example.configuration;

import com.example.session.Session;
import com.example.session.SessionObjectFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Configuration;

/**
 * @author tanwenhai@bilibili.com
 */
@Configuration
public class ApplicationContextConfiguration implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        beanFactory.registerResolvableDependency(Session.class, new SessionObjectFactory());
    }
}
