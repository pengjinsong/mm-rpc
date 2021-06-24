package io.github.rpc.feign;

import feign.Logger;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;


/**
 * @Author: pjs
 * @Date:2020/4/26
 * @description: 创建feign对象
 * @ModifiedBy:
 */
public class MmFeignFactory {
    private MmFeignFactory(){
        throw new UnsupportedOperationException("not support constructor");
    }
    /**
     * 构建feign
     */
    private static class SingleHolder{
        private static Decoder decoder=new JacksonDecoder();
        private static Encoder encoder=new JacksonEncoder();
        private static Logger logger=new Logger.ErrorLogger();
        private static ErrorDecoder errorDecoder=new MmErrorDecoder();
        private static MmFeign feign=new MmFeign.Builder()
                .decoder(decoder)
                .encoder(encoder)
                .logger(logger)
                .logLevel(Logger.Level.BASIC)
                .errorDecoder(errorDecoder)
                .build();
    }
    public static MmFeign getSingleton(){
        return SingleHolder.feign;
    }
}
