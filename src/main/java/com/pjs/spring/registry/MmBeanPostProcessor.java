package com.pjs.spring.registry;


import com.pjs.annotation.MmRpcService;
import com.pjs.feign.MmFeign;
import com.pjs.feign.MmFeignFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;

/**
 * @Author:mht pjs
 * @Date:2020/4/26
 * @description: 构建接口的feign代理对象
 * @ModifiedBy:
 */
@Component
public class MmBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter implements InitializingBean, BeanFactoryAware {
    /**
     * feign对象
     */
    private MmFeign qmpFeign;
    private BeanFactory beanFactory;
    @Override
    public Object postProcessAfterInitialization(final Object bean,final String beanName) throws BeansException {
        /**
         * 如果是接口
         * 存在注解@code QmpRpcService
         * 创建代理对象
         */
        Class<?> beanClass = bean.getClass();
        ReflectionUtils.doWithFields(beanClass, new ReflectionUtils.FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                if (field.isAnnotationPresent(MmRpcService.class)){
                    Class<?> fieldType = field.getType();
                   if (!fieldType.isInterface()){
                       throw new IllegalArgumentException("rpc invoke must be interface");
                   }
                    MmRpcService annotation = field.getAnnotation(MmRpcService.class);
                    String url = annotation.url();
                    if (StringUtils.isEmpty(url)){
                        throw new IllegalArgumentException("the url must not be null");
                    }
                    Object object=qmpFeign.getFeignProxyTarget(fieldType,url);
                    field.setAccessible(true);
                    field.set(bean,object);
                }
            }
        });
        return super.postProcessAfterInitialization(bean,beanName);
    }

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        return super.postProcessBeforeInstantiation(beanClass, beanName);
    }

    /**
     * 初始化qmpFeign
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        this.qmpFeign= MmFeignFactory.getSingleton();
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory=beanFactory;
    }
}
