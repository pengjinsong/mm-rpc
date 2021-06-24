package io.github.rpc.feign;

import feign.Feign;
import feign.Logger;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;


/**
 * @Author: pjs
 * @Date:2020/4/26
 * @description: 使用feign调用远程http+json接口
 * @ModifiedBy:
 */
public class MmFeign {
    /**
     * 解码器
     */
    private  Decoder decoder;
    /**
     * 加码器
     */
    private  Encoder encoder;
    /**
     * 日志对象
     */
    private  Logger logger;
    /**
     * 日记级别
     */
    private Logger.Level logLevel;
    /**
     * 错误处理对象
     */
    private ErrorDecoder errorDecoder;
    /**
     * 拦截器对象
     */
    //private List<RequestInterceptor> requestInterceptors;

    /**
     * 有参构造器
     * @param decoder              解码器
     * @param encoder              加码器
     * @param logger               日志对象
     * @param logLevel             日记级别
     * @param errorDecoder         错误处理
     */
    public MmFeign(Decoder decoder,
                   Encoder encoder,
                   Logger logger,
                   Logger.Level logLevel,
                   ErrorDecoder errorDecoder
                              ) {
        this.decoder = decoder;
        this.encoder = encoder;
        this.logger = logger;
        this.logLevel = logLevel;
        this.errorDecoder = errorDecoder;

    }

    /**
     * 建造者
     */
    public static class Builder{
        private  Decoder decoder;
        private  Encoder encoder;
        private  Logger logger;
        private Logger.Level logLevel;
        private ErrorDecoder errorDecoder;
        public Builder decoder(Decoder decoder){
            this.decoder=decoder;
            return this;
        }
        public Builder encoder(Encoder encoder){
            this.encoder=encoder;
            return this;
        }
        public Builder logger(Logger logger){
            this.logger=logger;
            return this;
        }
        public Builder logLevel(Logger.Level logLevel){
            this.logLevel=logLevel;
            return this;
        }
        public Builder errorDecoder(ErrorDecoder errorDecoder){
            this.errorDecoder=errorDecoder;
            return this;
        }
        public MmFeign build( ){
            return new MmFeign(this.decoder,
                    this.encoder,
                    this.logger,
                    this.logLevel,
                    this.errorDecoder);
        }
    }
    /**
     * 获得feign代理对象
     * @param clazz   需要代理的接口
     * @param url     请求根地址
     * @return        代理对象
     */
    public Object getFeignProxyTarget(Class<?> clazz,String url){
        return Feign.builder()
                .decoder(decoder)
                .encoder(encoder)
                .logger(logger)
                .logLevel(logLevel)
                .errorDecoder(errorDecoder)
                .target(clazz,url);
    }
}
