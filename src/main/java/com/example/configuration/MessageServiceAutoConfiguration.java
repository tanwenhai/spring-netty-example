package com.example.configuration;

import com.example.annotation.EnableMessageService;
import com.example.annotation.MessageService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_SINGLETON;

/**
 * @author tanwenhai@bilibili.com
 */
public class MessageServiceAutoConfiguration implements BeanFactoryAware, ImportBeanDefinitionRegistrar, EnvironmentAware, ResourceLoaderAware {
    DefaultListableBeanFactory beanFactory;

    Environment environment;

    ResourceLoader resourceLoader;

    private String[] basePackages;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (DefaultListableBeanFactory)beanFactory;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    public void init() {
//        System.out.println(1);
//        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
//        provider.addIncludeFilter(new AnnotationTypeFilter(MessageService.class));
//        Set<BeanDefinition> beanDefinitionSet = provider.findCandidateComponents("com.example.services");
//        for (BeanDefinition beanDefinition : beanDefinitionSet) {
//            try {
//                Class<?> clazz = Class.forName(beanDefinition.getBeanClassName());
//                Method method = clazz.getMethod("parseFrom", ByteString.class);
//                methodCache.put(clazz.getName(), method);
//                log.info("message method cache put {} -> {}#{}", clazz.getName(), clazz.getName(), method.getName());
//            } catch (ClassNotFoundException e) {
//                e.printStackTrace();
//            } catch (NoSuchMethodException e) {
//                e.printStackTrace();
//            }
//        }
    }


    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        Map<String, Object> attributes = metadata.getAnnotationAttributes(EnableMessageService.class.getName(), true);
        basePackages = (String[])attributes.get("basePackages");

        registerMessageService();
    }

    private void registerMessageService() {
        ClassPathScanningCandidateComponentProvider scanner = getClassScanner();
        scanner.setResourceLoader(resourceLoader);
        scanner.addIncludeFilter(new AnnotationTypeFilter(MessageService.class));
        Set<BeanDefinition> beanDefinitionSet = new HashSet<>();
        // 包扫描
        for (String basePackage : basePackages) {
            beanDefinitionSet.addAll(scanner.findCandidateComponents(basePackage));
        }

        for (BeanDefinition beanDefinition : beanDefinitionSet) {
            if (beanDefinition instanceof AnnotatedBeanDefinition) {
                registerBean((AnnotatedBeanDefinition) beanDefinition);
            }
        }
    }

    private void registerBean(AnnotatedBeanDefinition beanDefinition) {
        beanDefinition.setScope(SCOPE_SINGLETON);
        String beanName = (String)beanDefinition.getMetadata().getAnnotationAttributes(MessageService.class.getName()).get("value");
        if (Objects.equals("", beanName)) {
            beanName = beanDefinition.getBeanClassName();
        }
        beanFactory.registerBeanDefinition(beanName, beanDefinition);
    }

    private ClassPathScanningCandidateComponentProvider getClassScanner() {
        return new ClassPathScanningCandidateComponentProvider(false, environment);
    }
}
