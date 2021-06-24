package io.github.rpc;

import feign.Feign;
import feign.Logger;
import feign.Response;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import feign.httpclient.ApacheHttpClient;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;

/**
 * @Author:boy
 * @Date:2020/4/26
 * @description:
 * @ModifiedBy:
 */
public class FeignTest {
    public static void main(String[] args) {
        Encoder encoder = new JacksonEncoder();
        Decoder decoder = new JacksonDecoder();
         Feign.builder()
                 .client(new ApacheHttpClient())
                .encoder(encoder)
                .decoder(decoder)
                .logger(new Logger.ErrorLogger())
                .logLevel(Logger.Level.BASIC);


    }
    private static class QmpFeignErrorDecoder implements ErrorDecoder{

        @Override
        public Exception decode(String methodKey, Response response) {
            return null;
        }
    }
}
