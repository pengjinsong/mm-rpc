package com.pjs.spring.registry;

import com.pjs.annotation.MmRpcService;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyResourceConfigurer;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.core.Ordered;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * @Author:mht pjs
 * @Date:2020/4/26
 * @description: 扫描自定义注解
 * @ModifiedBy:
 */

public class MmScannerConfigurer implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware, BeanNameAware, Ordered {
    private String basePackages;
    private ApplicationContext applicationContext;
    private Class<? extends Annotation> annotationClass;
    private String beanName;
    private boolean processPropertyPlaceHolder;
    private BeanNameGenerator nameGenerator;
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        //处理配置内容
        processPropertyPlaceHolders();
        //实例化scanner
        MmClassPathMmScanner scanner = new MmClassPathMmScanner(registry);
        if (annotationClass==null){
            annotationClass=MmRpcService.class;
        }
        scanner.setAnnotationClass(annotationClass);
        if (beanName==null){
            nameGenerator=new AnnotationBeanNameGenerator();
        }
        scanner.setBeanNameGenerator(nameGenerator);
        scanner.registerFilters();
        //执行扫描
        scanner.doScan(StringUtils.tokenizeToStringArray(this.basePackages, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS));

    }

    /**
     * 处理配置内容
     */
    private void processPropertyPlaceHolders() {
        Map<String, PropertyResourceConfigurer> prcm = applicationContext.getBeansOfType(PropertyResourceConfigurer.class);
        // 属性配置没有暴露任何方法处理合适的配置内容
        //因此创建一个仅包含指定包的beanFactory,执行factory后处理方法
        if (!prcm.isEmpty()&&applicationContext instanceof ConfigurableApplicationContext){
            BeanDefinition beanDefinition=((ConfigurableApplicationContext)applicationContext)
                    .getBeanFactory()
                    .getBeanDefinition(beanName);
            DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
            beanFactory.registerBeanDefinition(beanName,beanDefinition);
            for (PropertyResourceConfigurer prc : prcm.values()) {
                prc.postProcessBeanFactory(beanFactory);
            }
            //获得扫描配置类（即当前类）配置文件配置信息
            MutablePropertyValues values = beanDefinition.getPropertyValues();
            //获得配置的包路径
            this.basePackages=updatePropertyValue("basePackage",values);
        }
    }

    private String updatePropertyValue(String propertyName, MutablePropertyValues values) {
        PropertyValue propertyValue = values.getPropertyValue(propertyName);
        if (propertyValue==null){
            return null;
        }
        Object value = propertyValue.getValue();
        if (value==null){
            return null;
        }else if (value instanceof String){
            return value.toString();
        }else if (value instanceof TypedStringValue){
            return ((TypedStringValue)value).getValue();
        }else {
            return null;
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        //no opt
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }


    @Override
    public void setBeanName(String name) {
        this.beanName=name;
    }

    public String getBasePackages() {
        return basePackages;
    }

    public void setBasePackages(String basePackages) {
        this.basePackages = basePackages;
    }

    public String getBeanName() {
        return beanName;
    }

    public boolean processPropertyPlaceHolder() {
        return processPropertyPlaceHolder;
    }

    public void setProcessPropertyPlaceHolder(boolean processPropertyPlaceHolder) {
        this.processPropertyPlaceHolder = processPropertyPlaceHolder;
    }

    public BeanNameGenerator getNameGenerator() {
        return nameGenerator;
    }

    public void setNameGenerator(BeanNameGenerator nameGenerator) {
        this.nameGenerator = nameGenerator;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }
}
