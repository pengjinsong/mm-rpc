package io.github.rpc.feign;

import feign.Logger;
import feign.slf4j.Slf4jLogger;

/**
 * @Author:pjs
 * @description:
 * @ModifiedBy:
 */
public class DefaultLogFactory implements FeignLogFactory {
    private Logger logger;

    public DefaultLogFactory(Logger logger) {
        this.logger = logger;
    }

    @Override
    public Logger create(Class<?> type) {
        return this.logger!=null?this.logger:new Slf4jLogger(type);
    }
}
