package com.pjs.feign;

import feign.Logger;

/**
 * @Author: pjs
 * @description:
 * @ModifiedBy:
 */
public interface FeignLogFactory {
    /**
     * create logger by factory method
     * @param type
     * @return {@link Logger}
     */
    Logger create(Class<?> type);
}
