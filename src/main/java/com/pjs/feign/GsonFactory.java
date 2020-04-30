package com.pjs.feign;

import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;

/**
 * @Author:pjs
 * @Date:2020/4/30
 * @description:
 * @ModifiedBy:
 */
public class GsonFactory {
    private static GsonEncoder gsonEncoder=new GsonEncoder();
    private static GsonDecoder gsonDecoder=new GsonDecoder();
    private GsonFactory(){new UnsupportedOperationException();}
    public static GsonDecoder decoder(){
        return gsonDecoder;
    }
    public static GsonEncoder encoder() {
        return gsonEncoder;
    }
}
