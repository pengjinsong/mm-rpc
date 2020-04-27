package com.pjs.annotation;

import java.lang.annotation.*;

/**
 * @Author: PJS
 * @Date:2020/4/26
 * @description: http+json方式远程调用注解
 * @ModifiedBy:
 */
@Target({ElementType.TYPE,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface MmRpcService {
    /**
     * 请求地址
     * @return
     */
    String url();
}
