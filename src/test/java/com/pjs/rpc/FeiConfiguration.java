package com.pjs.rpc;

import com.pjs.annotation.EnableMmRpc;
import com.pjs.feign.MmErrorDecoder;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @Author:pjs
 * @Date:2020/4/26
 * @description:
 * @ModifiedBy:
 */
@Configuration
@ComponentScan(basePackages = {"com.pjs.rpc"})
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
}

