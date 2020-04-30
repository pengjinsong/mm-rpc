package com.pjs.spring.registry;


import com.pjs.annotation.EnableMmRpc;
import com.pjs.annotation.MmRpcService;
import com.pjs.spring.MmFeignFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @Author:PJS
 * @Date:2020/4/26
 * @description: 扫描自定义注解，并实例化代理feign
 * @ModifiedBy:
 */

public class MmServiceBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {
    private static final Logger logger= LoggerFactory.getLogger(MmServiceBeanDefinitionRegistrar.class);
    private ResourceLoader resourceLoader;

    private Environment environment;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        //注册配置信息 TODO
        registryDefaultConfiguration(metadata, registry);
        //扫描包 并实例化bean
        registryService(metadata, registry);
    }

    private void registryDefaultConfiguration(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        Map<String, Object> annotationAttributes = metadata.getAnnotationAttributes(EnableMmRpc.class.getName(), true);
        //处理配置类 TODO
    }

    /**
     * 获得注解信息 代理接口生成bean
     *
     * @param metadata
     * @param registry
     */
    private void registryService(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        ClassPathScanningCandidateComponentProvider scanner = getScanner();
        scanner.setResourceLoader(resourceLoader);
        Set<String> basePackages = null;
        //指定扫描注解
        AnnotationTypeFilter annotationTypeFilter = new AnnotationTypeFilter(MmRpcService.class);
        //获得扫描包
        basePackages = getBasePackages(metadata);
        scanner.addIncludeFilter(annotationTypeFilter);
        //执行扫描
        for (String basePackage : basePackages) {
            Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents(basePackage);
            for (BeanDefinition candidateComponent : candidateComponents) {
                if (candidateComponent instanceof AnnotatedBeanDefinition) {
                    AnnotatedBeanDefinition beanDefinition = (AnnotatedBeanDefinition) candidateComponent;
                    AnnotationMetadata annotationMetadata = beanDefinition.getMetadata();
                    Assert.isTrue(annotationMetadata.isInterface(),
                            "@QmpRpcService can only be specified on an interface");
                    Map<String, Object> annotationAttributes = annotationMetadata.getAnnotationAttributes(MmRpcService.class.getCanonicalName());
                    if (logger.isDebugEnabled()){
                        logger.debug("scanner mmRpc interface {}",beanDefinition.getBeanClassName());
                    }
                    registryQmpService(registry, annotationMetadata, annotationAttributes);
                }
            }
        }
    }

    /**
     * 创建代理feign Bean
     * @param registry
     * @param annotationMetadata
     * @param annotationAttributes
     */
    private void registryQmpService(BeanDefinitionRegistry registry, AnnotationMetadata annotationMetadata, Map<String, Object> annotationAttributes) {
        //获得扫描到的接口全限定名
        String className = annotationMetadata.getClassName();

        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(MmFeignFactoryBean.class);
        String url = (String) annotationAttributes.get("url");

        builder.addPropertyValue("url", url);
        builder.addPropertyValue("type", className);

        builder.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);

        AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();

        String name = annotationMetadata.getClassName();
        name=name.substring(name.lastIndexOf("."));
        String alias = name + "RpcService";

        String qualifier = getQualifier(annotationAttributes);
        if (StringUtils.hasText(qualifier)) {
            alias = qualifier;
        }

        //have a default not null
        Boolean primary = (Boolean)annotationAttributes.get("primary");
        beanDefinition.setPrimary(primary);

        if (logger.isDebugEnabled()){
            logger.debug("===> registry rpc interface {} proxy by feign",annotationMetadata.getClassName());
        }
        BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(beanDefinition, className, new String[]{alias});

        BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, registry);
    }

    /**
     * 获取注解上的包路径
     *
     * @param metadata
     * @return
     */
    private Set<String> getBasePackages(AnnotationMetadata metadata) {
        Map<String, Object> attrs = metadata.getAnnotationAttributes(EnableMmRpc.class.getCanonicalName());
        Set<String> basePackages = new HashSet<>();
        for (String pkg : (String[]) attrs.get("value")) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }
        for (String pkg : (String[]) attrs.get("basePackages")) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }
        for (Class<?> clazz : (Class<?>[]) attrs.get("basePackageClasses")) {
            basePackages.add(ClassUtils.getPackageName(clazz));
        }
        if (basePackages.isEmpty()) {
            basePackages.add(ClassUtils.getPackageName(metadata.getClassName()));
        }
        return basePackages;
    }

    private String getQualifier(Map<String, Object> attrs) {
        if (attrs == null) {
            return null;
        }
        String qualifier = (String) attrs.get("qualifier");
        if (StringUtils.hasText(qualifier)) {
            return qualifier;
        }
        return null;
    }

    /**
     * 创建包扫描器
     * 指定要扫描的类型
     *
     * @return
     */
    protected ClassPathScanningCandidateComponentProvider getScanner() {
        return new ClassPathScanningCandidateComponentProvider(false, this.environment) {
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                boolean isCandidate = false;
                if (beanDefinition.getMetadata().isIndependent()) {
                    if (!beanDefinition.getMetadata().getAnnotationTypes().isEmpty()) {
                        isCandidate = true;
                    }
                }
                return isCandidate;
            }
        };
    }
}
