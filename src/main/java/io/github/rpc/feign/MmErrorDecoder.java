package io.github.rpc.feign;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author:mht pjs
 * @Date:2020/4/26
 * @description: feign调用错误处理
 * @ModifiedBy:
 */
public class MmErrorDecoder implements ErrorDecoder {
    private static final Logger logger= LoggerFactory.getLogger(MmErrorDecoder.class);
    @Override
    public Exception decode(String methodKey, Response response) {
        logger.error("===> feign invoker error,method: {}; responseCode: {}; message: {}",methodKey,response.status(),response.body().toString());
        return new Default().decode(methodKey,response);
    }
}
