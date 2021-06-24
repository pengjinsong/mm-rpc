package io.github.rpc.configuration;

import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import io.github.rpc.feign.MmErrorDecoder;
import feign.Contract;
import feign.RequestInterceptor;
import feign.auth.BasicAuthRequestInterceptor;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import io.github.rpc.feign.SpringContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *  默认配置
 * @Author:boy
 * @Date:2020/5/6
 * @description:
 * @ModifiedBy:
 */
@Configuration
public class ClientDefaultConfiguration {
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

    @Bean
    public Contract contract(){
        return new SpringContract();
    }
}
