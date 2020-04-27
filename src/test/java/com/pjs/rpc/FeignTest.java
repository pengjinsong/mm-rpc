package com.pjs.rpc;

import feign.Feign;
import feign.Logger;
import feign.Response;
import feign.codec.ErrorDecoder;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;

import java.util.Map;

/**
 * @Author:boy
 * @Date:2020/4/26
 * @description:
 * @ModifiedBy:
 */
public class FeignTest {
    public static void main(String[] args) {
        GsonEncoder encoder = new GsonEncoder();
        GsonDecoder decoder = new GsonDecoder();
        /*JobInfoFeign target = Feign.builder()
                .encoder(encoder)
                .decoder(decoder)
                .logger(new Logger.ErrorLogger())
                .logLevel(Logger.Level.BASIC)
                .target(JobInfoFeign.class, "http://localhost:8088/xxl-job-admin");
        Map<String, Object> map = target.pageList(0, 10, 0, null, null);
        System.out.println(map);*/
        Class<? extends GsonEncoder> aClass = encoder.getClass();
        System.out.println(aClass.getCanonicalName());
        System.out.println(aClass.getSimpleName());
        System.out.println(aClass.getName());
        System.out.println(aClass.getPackage());
        System.out.println(aClass.getTypeName());
        System.out.println(aClass.getAnnotations());
    }
    private static class QmpFeignErrorDecoder implements ErrorDecoder{

        @Override
        public Exception decode(String methodKey, Response response) {
            return null;
        }
    }
}
