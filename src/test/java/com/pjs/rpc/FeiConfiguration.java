package com.pjs.rpc;

import com.pjs.annotation.EnableMmRpc;
import com.pjs.feign.MmErrorDecoder;
import feign.RequestInterceptor;
import feign.auth.BasicAuthRequestInterceptor;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author:pjs
 * @Date:2020/4/26
 * @description:
 * @ModifiedBy:
 */
@Configuration
@EnableMmRpc(basePackages = "com.pjs.rpc")
public class FeiConfiguration {
    @Bean
    public Decoder decoder(){
        return new GsonDecoder();
    }
    @Bean
    public Encoder encoder(){
        return new GsonEncoder();
    }
    @Bean
    public ErrorDecoder errorDecoder(){
        return new MmErrorDecoder();
    }
    @Bean
    public RequestInterceptor requestInterceptor(){
        return new BasicAuthRequestInterceptor("","");
    }
}

