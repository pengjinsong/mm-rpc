package io.github.rpc;


import io.github.rpc.annotation.MmRpcService;
import feign.Param;
import feign.RequestLine;

import java.util.Map;

/**
 * @Author:boy
 * @Date:2020/4/26
 * @description:
 * @ModifiedBy:
 */
@MmRpcService(url ="http://localhost:8088/xx" )
public interface TestInterface {
    @RequestLine("GET /pageList?start={start}&length={length}")
    public Map<String, Object> pageList(@Param("start") int start,
                                        @Param("length")int length
                                       );
}
