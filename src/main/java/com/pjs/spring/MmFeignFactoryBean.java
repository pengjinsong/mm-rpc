package com.pjs.spring;

import com.pjs.feign.DefaultLogFactory;
import com.pjs.feign.FeignLogFactory;
import com.pjs.feign.GsonFactory;
import feign.Client;
import feign.Feign;
import feign.Logger;
import feign.RequestInterceptor;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.httpclient.ApacheHttpClient;
import feign.slf4j.Slf4jLogger;
import okhttp3.OkHttpClient;
import org.apache.http.client.HttpClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @Author:pjs
 * @Date:2020/4/27
 * @description:
 * @ModifiedBy:
 */
public class MmFeignFactoryBean implements FactoryBean<Object>, InitializingBean, ApplicationContextAware {
    /**
     * spring context
     */
    private ApplicationContext applicationContext;

    private Client client;
    /**
     * 代理接口
     */
    private Class<?> type;
    /**
     * 请求url
     */
    private String url;
    /**
     * 反序列化
     */
    private Decoder decoder;
    /**
     * 序列化
     */
    private Encoder encoder;
    /**
     * 日志
     */
    private Logger logger;
    /**
     * 日志级别
     */
    private Logger.Level logLevel;
    /**
     * 错误处理
     */
    private ErrorDecoder errorDecoder;
    /**
     * feign拦截器
     */
    private List<RequestInterceptor> interceptors = new LinkedList<>();
    /**
     * 日志工厂
     */
    private FeignLogFactory feignLogFactory;

    @Override
    public Object getObject() throws Exception {
        return createFeignTarget();
    }

    private Object createFeignTarget() {
        Feign.Builder builder = new Feign.Builder();
        builder = builder
                .decoder(decoder)
                .encoder(encoder)
                .logger(logger)
                .logLevel(logLevel)
                .requestInterceptors(interceptors);
        if (client != null) {
            builder.client(client);
        }
        if (errorDecoder!=null){
            builder.errorDecoder(errorDecoder);
        }
        return builder.target(type, url);
    }

    @Override
    public Class<?> getObjectType() {
        return this.type;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.hasText(url, "远程调用接口必须设置url地址");
        Assert.notNull(type, "type must not be null");
        decoder = applicationContext.getBean(Decoder.class);
        encoder = applicationContext.getBean(Encoder.class);
        errorDecoder = applicationContext.getBean(ErrorDecoder.class);
        HttpClient httpClient = applicationContext.getBean(HttpClient.class);
        OkHttpClient okHttpClient = applicationContext.getBean(OkHttpClient.class);
        if (null != okHttpClient) {
            client = new feign.okhttp.OkHttpClient(okHttpClient);
        } else if (null != httpClient) {
            client = new ApacheHttpClient(httpClient);
        }
        feignLogFactory=new DefaultLogFactory(logger);
        logger=feignLogFactory.create(type);
        if (null == logLevel) {
            logLevel = Logger.Level.BASIC;
        }
        //添加拦截器
        Map<String, RequestInterceptor> beans = applicationContext.getBeansOfType(RequestInterceptor.class);
        for (RequestInterceptor interceptor : beans.values()) {
            addInterceptor(interceptor);
        }
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setDecoder(Decoder decoder) {
        this.decoder = decoder;
    }

    public void setEncoder(Encoder encoder) {
        this.encoder = encoder;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public void setLogLevel(Logger.Level logLevel) {
        this.logLevel = logLevel;
    }

    public void setErrorDecoder(ErrorDecoder errorDecoder) {
        this.errorDecoder = errorDecoder;
    }

    public Class<?> getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public Decoder getDecoder() {
        return decoder;
    }

    public Encoder getEncoder() {
        return encoder;
    }

    public Logger getLogger() {
        return logger;
    }

    public Logger.Level getLogLevel() {
        return logLevel;
    }

    public ErrorDecoder getErrorDecoder() {
        return errorDecoder;
    }

    public void addInterceptor(RequestInterceptor interceptor) {
        this.interceptors.add(interceptor);
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public FeignLogFactory getFeignLogFactory() {
        return feignLogFactory;
    }

    public void setFeignLogFactory(FeignLogFactory feignLogFactory) {
        this.feignLogFactory = feignLogFactory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MmFeignFactoryBean that = (MmFeignFactoryBean) o;

        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (url != null ? !url.equals(that.url) : that.url != null) return false;
        if (decoder != null ? !decoder.equals(that.decoder) : that.decoder != null) return false;
        if (encoder != null ? !encoder.equals(that.encoder) : that.encoder != null) return false;
        if (logger != null ? !logger.equals(that.logger) : that.logger != null) return false;
        if (logLevel != that.logLevel) return false;
        return errorDecoder != null ? errorDecoder.equals(that.errorDecoder) : that.errorDecoder == null;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (decoder != null ? decoder.hashCode() : 0);
        result = 31 * result + (encoder != null ? encoder.hashCode() : 0);
        result = 31 * result + (logger != null ? logger.hashCode() : 0);
        result = 31 * result + (logLevel != null ? logLevel.hashCode() : 0);
        result = 31 * result + (errorDecoder != null ? errorDecoder.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "QmpFeignFactoryBean{" +
                "type=" + type +
                ", url='" + url + '\'' +
                ", decoder=" + decoder +
                ", encoder=" + encoder +
                ", logger=" + logger +
                ", logLevel=" + logLevel +
                ", errorDecoder=" + errorDecoder +
                '}';
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
