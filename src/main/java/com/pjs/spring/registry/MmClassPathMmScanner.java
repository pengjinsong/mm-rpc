package com.pjs.spring.registry;


import com.pjs.spring.MmFeignFactoryBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 * @Author:pjs
 * @Date:2020/4/26
 * @description: 自定义扫描类
 * @ModifiedBy:
 */
public class MmClassPathMmScanner extends ClassPathBeanDefinitionScanner {

    private Class<? extends Annotation> annotationClass;

    private FactoryBean<?> factoryBean;

    public MmClassPathMmScanner(BeanDefinitionRegistry registry) {
        super(registry, false);
    }


    @Override
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> definitionHolders = super.doScan(basePackages);
        if (definitionHolders.isEmpty()) {
            logger.warn("No service was found in '" + Arrays.toString(basePackages) + "' package. Please check your configuration.");
        } else {
            processBeanDefinitions(definitionHolders);
        }
        return definitionHolders;
    }

    private void processBeanDefinitions(Set<BeanDefinitionHolder> definitionHolders) {
        GenericBeanDefinition definition;
        String url = "";
        String type="";
        for (BeanDefinitionHolder definitionHolder : definitionHolders) {
            definition = (GenericBeanDefinition) definitionHolder.getBeanDefinition();
            if (logger.isDebugEnabled()) {
                logger.debug("Creating proxyBean with name '" + definitionHolder.getBeanName()
                        + "' and '" + definition.getBeanClassName() + "' interface");
            }
            type=definition.getBeanClassName();
            if (definition instanceof AnnotatedBeanDefinition){
                AnnotatedBeanDefinition annotatedBeanDefinition= (AnnotatedBeanDefinition) definition;
                Map<String, Object> attrs = annotatedBeanDefinition.getMetadata().getAnnotationAttributes(this.annotationClass.getCanonicalName());
                url= (String) attrs.get("url");
            }

            definition.setBeanClass(this.factoryBean.getClass());
            definition.getPropertyValues().add("url", url);
            definition.getPropertyValues().add("type",type);

            definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
        }

    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent();
    }

    public Class<? extends Annotation> getAnnotationClass() {
        return annotationClass;
    }

    public FactoryBean<?> getFactoryBean() {
        return factoryBean = factoryBean != null ? factoryBean : new MmFeignFactoryBean();
    }

    public void setFactoryBean(FactoryBean<?> factoryBean) {
        this.factoryBean = factoryBean;
    }

    public void setAnnotationClass(Class<? extends Annotation> annotationClass) {
        this.annotationClass = annotationClass;
    }

    public void registerFilters() {
        if (this.annotationClass != null) {
            addIncludeFilter(new AnnotationTypeFilter(this.annotationClass) {
                @Override
                protected boolean matchClassName(String className) {
                    return false;
                }
            });
        }
    }
}
