package com.pjs.rpc;


import com.pjs.annotation.MmRpcService;
import feign.Param;
import feign.RequestLine;

import java.util.Map;

/**
 * @Author:boy
 * @Date:2020/4/26
 * @description:
 * @ModifiedBy:
 */
@MmRpcService(url ="http://localhost:8088/xxl-job-admin" )
public interface TestInterface {
    @RequestLine("GET /pageList?start={start}&length={length}&executorHandler={jobGroup}&filterTime={filterTime}")
    public Map<String, Object> pageList(@Param("start") int start,
                                        @Param("length")int length,
                                        @Param("jobGroup") int jobGroup,
                                        @Param("executorHandler") String executorHandler,
                                        @Param("filterTime") String filterTime);
}
