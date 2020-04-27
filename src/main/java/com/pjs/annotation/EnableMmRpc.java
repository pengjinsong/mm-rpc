package com.pjs.annotation;


import com.pjs.spring.registry.ServiceBeanDefinitionRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @Author: pjs
 * @Date:2020/4/26
 * @description: 扫描声明的 ${@link MmRpcService} 接口
 * @ModifiedBy:
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(ServiceBeanDefinitionRegistrar.class)
public @interface EnableMmRpc {
    /**
     * 包的别名
     *
     * @return
     */
    String[] value() default {};

    /**
     * 扫描包
     *
     * @return
     */
    String[] basePackages() default {};

    /**
     * 扫描类
     *
     * @return
     */
    Class<?>[] basePackageClasses() default {};

    /**
     * 配置文件 TODO
     *
     * @return
     */
    Class<?>[] defaultConfiguration() default {};

    /**
     * 指定接口 TODO
     *
     * @return
     */
    Class<?>[] clients() default {};
}
