# Druid - 阿里云团队出品，为监控而生的数据库连接池

## 添加依赖 [![Maven Central](https://img.shields.io/maven-central/v/com.alibaba/druid-spring-boot-starter?style=flat-square)](https://github.com/alibaba/druid/tree/master/druid-spring-boot-starter)

```xml
<dependency>
   <groupId>com.alibaba</groupId>
   <artifactId>druid-spring-boot-starter</artifactId>
   <version>${alibaba-druid.version}</version>
</dependency>
```

## 配置属性

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:recommend
    username: sa
    password:
    ##
    # 设置数据源类型为 DruidDataSource
    ##
    type: com.alibaba.druid.pool.DruidDataSource
    druid:
      ##
      # 配置初始化大小、最小、最大连接池数量
      # - min-idle：池中维护的最小空闲连接数，默认为 0 个
      # - max-active：池中最大连接数，包括闲置和使用中的连接，默认为 8 个；推荐配置：20，多数场景下 20 已完全够用，当然这个参数跟使用场景相关性很大，一般配置成正常连接数的 3~5 倍。
      ##
      initial-size: 5
      min-idle: 10
      max-active: 20
      ##
      # 参数表示是否对空闲连接保活，布尔类型。
      #
      # 那么需要保活连接，是不是将 keepAlive 配置成 true 就完事了呢？
      # 虽然 true 的确是开启了保活机制，但是应该保活多少个，心跳检查的规则是什么，这些都需要正确配置，否则还是可能事与愿违。
      # 这里需要了解几个相关的参数：minIdle 最小连接池数量，连接保活的数量，空闲连接超时踢除过程会保留的连接数（前提是当前连接数大于等于 minIdle），其实 keepAlive 也仅维护已存在的连接，而不会去新建连接，即使连接数小于 minIdle；
      # minEvictableIdleTimeMillis 单位毫秒，连接保持空闲而不被驱逐的最小时间，保活心跳只对存活时间超过这个值的连接进行；
      # maxEvictableIdleTimeMillis 单位毫秒，连接保持空闲的最长时间，如果连接执行过任何操作后计时器就会被重置（包括心跳保活动作）；
      # timeBetweenEvictionRunsMillis 单位毫秒，Destroy 线程检测连接的间隔时间，会在检测过程中触发心跳。保活检查的详细流程可参见源码 com.alibaba.druid.pool.DruidDataSource.DestroyTask，其中心跳检查会根据配置使用 ping 或 validationQuery 配置的检查语句。
      #
      # 推荐配置：如果网络状况不佳，程序启动慢或者经常出现突发流量，则推荐配置为 true；
      ##
      keep-alive: true
      # 配置一个连接在池中最小生存的时间，单位是毫秒
      min-evictable-idle-time-millis: 600000
      max-evictable-idle-time-millis: 900000
      # 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
      time-between-eviction-runs-millis: 2000
      ##
      # 配置获取连接等待超时的时间
      #
      # 推荐配置:
      # - 内网（网络状况好）800；
      # - 网络状况不是特别好的情况下推荐大于等于 1200，因为 tcp 建连重试一般是 1 秒；
      ##
      max-wait: 800
      ##
      # 可以配置 connectTimeout 和 socketTimeout，它们的单位都是毫秒，这两个参数在应对网络异常方面非常重要。
      # - connectTimeout 配置建立 TCP 连接的超时时间；
      # - socketTimeout 配置发送请求后等待响应的超时时间；
      # 这两个参数也可以通过在 jdbc url 中添加 connectTimeout=xxx&socketTimeout=xxx 的方式配置，试过在 connectinoProperties 中和 jdbc url两个地方都配置，发现优先使用 connectionProperties 中的配置。
      # 如果不设置这两项超时时间，服务会有非常高的风险。现实案例是在网络异常后发现应用无法连接到 DB，但是重启后却能正常的访问 DB。因为在网络异常下 socket 没有办法检测到网络错误，这时连接其实已经变为“死连接”，如果没有设置 socket 网络超时，连接就会一直等待 DB 返回结果，造成新的请求都无法获取到连接。
      #
      # 推荐配置：socketTimeout=3000;connectTimeout=1200
      ##
      connect-properties: socketTimeout=3000;connectTimeout=1200
      ##
      # 用于检测连接是否有效的 SQL 语句
      ##
      validation-query: select 1
      ##
      # 申请连接的时候检测，如果空闲时间大于 timeBetweenEvictionRunsMillis，执行 validationQuery 检测连接是否有效
      ##
      test-while-idle: true
      ##
      # 申请连接时执行 validationQuery 检测连接是否有效，做了这个配置会降低性能
      ##
      test-on-borrow: false
      ##
      # 归还连接时执行 validationQuery 检测连接是否有效，做了这个配置会降低性能
      ##
      test-on-return: false
      ##
      # 通过限制达到一定使用次数后断开重连，使得多个服务器间负载更均衡
      ##
      phy-max-use-count: 1000
```

## 多数据源

### 添加配置
```yaml
spring:
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    druid:
      ##
      # 合并多个 DruidDataSource 的监控数据
      ##
      use-global-data-source-stat: true
      ##
      # Druid 数据源 1 配置
      ##
      foo:
        url: jdbc:h2:mem:foo
        username: foo
        password:
        initial-size: 5
        min-idle: 10
        max-active: 20
        maxWait: 800
      ##
      # Druid 数据源 2 配置
      ##
      bar:
        url: jdbc:h2:mem:bar
        username: bar
        password:
        initial-size: 5
        min-idle: 10
        max-active: 20
        maxWait: 1200
```

### 创建数据源

```java
@Configuration
public class DruidConfig {

  @Bean
  @Primary
  @ConfigurationProperties("spring.datasource.druid.foo")
  public DataSource dataSourceFoo() {
    return DruidDataSourceBuilder.create().build();
  }

  @Bean
  @ConfigurationProperties("spring.datasource.druid.bar")
  public DataSource dataSourceBar() {
    return DruidDataSourceBuilder.create().build();
  }
}
```

## 配置 Filter

```yaml
spring:
  datasource:
    druid:
      filters: config,stat,slf4j
      filter:
        config:
          enabled: true
        ##
        # 配置 StatFilter，参考文档：https://github.com/alibaba/druid/wiki/%E9%85%8D%E7%BD%AE_StatFilter
        ##
        stat:
          enabled: true
          # 开启慢查询记录
          log-slow-sql: true
          # 慢 SQL 的标准，单位：毫秒
          slow-sql-millis: 5000
        slf4j:
          enabled: true
        wall:
          enabled: true
      ##
      # 配置 StatViewServlet，参考文档：https://github.com/alibaba/druid/wiki/%E9%85%8D%E7%BD%AE_StatViewServlet%E9%85%8D%E7%BD%AE
      # 访问 http://127.0.0.1:8080/druid 地址，可以看到监控页面
      ##
      stat-view-servlet:
        enabled: true
        login-username: yangbin
        login-password: yangbin
```

## 监控数据

```java
@RestController
public class DruidStatController {

  @GetMapping("/druid/stat")
  public Object druidStat() {
    return DruidStatManagerFacade.getInstance().getDataSourceStatDataList();
  }
}
```

## 🔗️ 参考链接

- https://github.com/alibaba/druid/tree/master/druid-spring-boot-starter
- https://github.com/alibaba/druid/wiki/DruidDataSource%E9%85%8D%E7%BD%AE
- [Druid 连接池配置](https://www.alibabacloud.com/help/zh/doc-detail/123739.htm)
- [数据库连接池配置（案例及排查指南）](https://tech.youzan.com/shu-ju-ku-lian-jie-chi-pei-zhi/)
- [芋道 Spring Boot 数据库连接池入门](https://www.iocoder.cn/Spring-Boot/datasource-pool/)
