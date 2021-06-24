### 介绍
- 简化传统SSM框架之间的HTTP+JSON调用

- 借鉴cloud-openFeign

- 对feign的封装，

- 自带Gson 序列化和反序列化，需手动注入到spring容器中

- 默认日志对象为SL4J

- 默认日志级别为 BASIC

- 默认错误处理为 ErrorLogDecoder.Default

- 可使用Bean方式注入拦截器

- 示例

- 配置

- ```java
  @Configuration
  @EnableMmRpc(basePackages = "com.pjs.rpc")
  public class FeiConfiguration {
      @Bean
      public Decoder decoder(){
          return new GsonDecoder();
      }
      @Bean
      public Encoder encoder(){
          return new GsonEncoder();
      }
      @Bean
      public ErrorDecoder errorDecoder(){
          return new MmErrorDecoder();
      }
      @Bean
      public RequestInterceptor requestInterceptor(){
          return new BasicAuthRequestInterceptor("","");
      }
  }
  ```

- 使用

- ```java
  @MmRpcService(url ="http://localhost:8080/xx" )
  public interface TestInterface {
      @RequestLine("GET /pageList?start={start}&length={length}")
      public Map<String, Object> pageList(@Param("start") int start,
                                          @Param("length")int length,
                                         );
  ```

- 
### 1.1.0 
- 提供默认配置
- 引入spring4 注解支持

```java
import io.github.rpc.configuration.ClientDefaultConfiguration;
import org.springframework.context.annotation.Import;
@Configuration
@Import(ClientDefaultConfiguration.class)
@EnableMmRpc(basePackages = "com.pjs.rpc")
  public class FeiConfiguration {
     
  }
```
```java


import io.github.rpc.annotation.MmRpcService;import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
@MmRpcService("http://")
public interface TestFeign{
    @GetMapping("/test")
    @ResponseBody
    AjaxJson test(@RequestParam("name") String name);
}
```