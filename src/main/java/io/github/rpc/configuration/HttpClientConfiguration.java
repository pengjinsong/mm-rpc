package io.github.rpc.configuration;

import org.apache.http.HttpConnectionFactory;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.DefaultBHttpClientConnectionFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * @Description httpclient配置
 * @Author pjs
 * @Date 2021/6/24 11:55
 */
@Configuration
public class HttpClientConfiguration {
    public static final Logger logger = LoggerFactory.getLogger(HttpClientConfiguration.class);
    //connectionManager
    private static final int MAX_PER_ROUTE = 20;
    private static final int MAX_CONNECTIONS = 200;
    private static final long TIME_TO_LIVE = 900L;
    private static final boolean DISABLE_SSL = false;
    private static final String HTTP_SCHEMA = "http";
    private static final String HTTPS_SCHEMA = "https";
    public static final int DEFAULT_CONNECTION_TIMER_REPEAT = 3000;
    //requestConfig
    private static final int CONNECT_TIMEOUT = 200;
    private static final boolean ALLOW_REDIRECT = true;

    private CloseableHttpClient httpClient;

    //定时器
    private static final Timer connectTimer = new Timer("feign-httpclient-connectManager-timer", true);

    @Bean
    public CloseableHttpClient httpClient() {
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(CONNECT_TIMEOUT)
                .setRedirectsEnabled(ALLOW_REDIRECT)
                .build();
        this.httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(config)
                .disableContentCompression()
                .disableCookieManagement()
                .useSystemProperties()
                .setConnectionManager(connectionManager())
                .build();
        return this.httpClient;
    }

    @Bean
    public HttpClientConnectionManager connectionManager() {
        RegistryBuilder register = RegistryBuilder.create()
                .register(HTTP_SCHEMA, PlainConnectionSocketFactory.INSTANCE);
        if (!DISABLE_SSL) {
            register.register(HTTPS_SCHEMA, SSLConnectionSocketFactory.getSocketFactory());
        }
        Registry registry = register.build();
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(registry,
                null, null, null, TIME_TO_LIVE, TimeUnit.SECONDS);
        connectionManager.setDefaultMaxPerRoute(MAX_PER_ROUTE);
        connectionManager.setMaxTotal(MAX_CONNECTIONS);
        //周期性关闭过期连接
        connectTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                connectionManager.closeExpiredConnections();
            }
        }, 30000, DEFAULT_CONNECTION_TIMER_REPEAT);
        return connectionManager;
    }

    @Bean
    public HttpConnectionFactory connectionFactory() {
        return DefaultBHttpClientConnectionFactory.INSTANCE;
    }

    @PreDestroy
    public void destroy() throws IOException {
        connectTimer.cancel();
        if (httpClient != null) {
            httpClient.close();
        }
    }
}
