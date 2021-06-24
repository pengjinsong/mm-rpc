package io.github.rpc;

import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import io.github.rpc.annotation.EnableMmRpc;
import io.github.rpc.feign.MmErrorDecoder;
import feign.RequestInterceptor;
import feign.auth.BasicAuthRequestInterceptor;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
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
        return new JacksonDecoder();
    }
    @Bean
    public Encoder encoder(){
        return new JacksonEncoder();
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

