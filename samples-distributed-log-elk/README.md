# 使用 ELK 进行日志的集中管理

## 日志的重要性

### 为什么重要

- 运维：医生给病人看病，日志就是病人对自己病情的陈述
- 恶意攻击、恶意注册、刷单、恶意密码猜测等

### 面对的挑战

- 关注点很多，任何一个点都有可能引起问题
- 日志分散在很多机器，出了问题时，才发现日志被删了
- 很多运维人员是消防员，哪里有问题去哪里

## 集中化日志管理

```txt
日志搜索 > 格式化分析 > 检索与可视化 > 风险告警
```

## 快速搭建 ELK 集成环境

### 技术选型

那么，[ELK 到底是什么呢？](https://www.elastic.co/cn/what-is/elk-stack)“ELK”是三个开源项目的首字母缩写，这三个项目分别是：

+ `E`（Elasticsearch）是一个搜索和分析引擎。
+ `L`（Logstash）是服务器端数据处理管道，能够同时从多个来源采集数据，转换数据，然后将数据发送到诸如 Elasticsearch 等“存储库”中。
+ `K`（Kibana）则可以让用户在 Elasticsearch 中使用图形和图表对数据进行可视化。

### 快速部署

使用 Docker 部署上面的基础环境，参考配置文件 [`docker-compose.yml`](./docker-compose.yml)，输入以下命令进行一键部署：

```bash
# 检查配置
docker-compose config
# 启动服务（-d 后台启动）
docker-compose up -d
# 停止并清除服务
docker-compose down
```

### 配置 Logstash

参考配置文件 [`logstash-config.conf`](./logstash-config.conf)，示例如下：

```
input {
  tcp {
    mode => "server"
    host => "0.0.0.0"
    port => 8080
    codec => json_lines
  }
}
output {
  elasticsearch {
    hosts => "elasticsearch:9200"
    # 索引名需参考 index templates 的配置，如：logs-*-*
    # index => "app-logs-%{app_name}-%{+YYYY.MM.dd}"
    index => "app-logs-%{+YYYY.MM.dd}"
  }
}
```

## Spring Boot 集成 ELK 进行日志管理

### 添加依赖 [![Maven Central](https://img.shields.io/maven-central/v/net.logstash.logback/logstash-logback-encoder?style=flat-square)](https://github.com/logstash/logstash-logback-encoder)

```xml
<dependency>
  <groupId>net.logstash.logback</groupId>
  <artifactId>logstash-logback-encoder</artifactId>
  <version>${logstash-logback-encoder.version}</version>
</dependency>
```

### 添加 logback 配置

#### 方式一：通过 logback-spring.xml 配置

参考配置文件 [logback-spring.xml](src/main/resources/logstash-logback-spring.xml)，示例如下：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
  <include resource="org/springframework/boot/logging/logback/base.xml" />

  <!-- 默认会被序列化到日志文档中 -->
  <springProperty scope="context" name="APP_NAME" source="spring.application.name"/>

  <appender name="LOGSTASH" class="net.logstash.logback.appender.LogstashTcpSocketAppender">
    <destination>192.168.50.88:8880</destination>
    <encoder charset="UTF-8" class="net.logstash.logback.encoder.LogstashEncoder" >
      <!-- customFields 的作用是在 Logstash 配置中指定索引名字时的可选参数，日志文档中会添加这个字段 -->
      <customFields>{"app_name":"${APP_NAME}"}</customFields>
    </encoder>
  </appender>

  <root level="INFO">
    <appender-ref ref="LOGSTASH" />
    <appender-ref ref="CONSOLE" />
  </root>

</configuration>
```

#### 方式二：通过 Java Config 配置（可定制 starter）

```java
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "elk.logstash", name = "enabled", havingValue = "true")
public class LogstashLogbackConfig {

  private static final String LOGSTASH_APPENDER_NAME = "LOGSTASH";
  private static final String LOGSTASH_ASYNC_APPENDER_NAME = "ASYNC_LOGSTASH";

  @Value("${spring.application.name}")
  private String appName;

  @Autowired
  private LogstashProperties logstash;

  @Bean
  @ConfigurationProperties(prefix = "elk.logstash")
  public LogstashProperties logstash() {
    return new LogstashProperties();
  }

  @PostConstruct
  private void addLogstashAppender() {
    LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

    log.info("Initializing LogstashAppender");
    final LogstashTcpSocketAppender logstashAppender = new LogstashTcpSocketAppender();
    logstashAppender.setName(LOGSTASH_APPENDER_NAME);
    logstashAppender.setContext(loggerContext);
    logstashAppender.addDestinations(
      new InetSocketAddress(this.logstash.getHost(), this.logstash.getPort())
    );

    // https://github.com/logstash/logstash-logback-encoder
    final LogstashEncoder logstashEncoder = new LogstashEncoder();
    logstashEncoder.setIncludeContext(false);
    String customFields = "{\"app_name\":\"" + this.appName +"\",\"idol\":\"yangbin\"}";
    logstashEncoder.setCustomFields(customFields);

    final ShortenedThrowableConverter throwableConverter = new ShortenedThrowableConverter();
    throwableConverter.setRootCauseFirst(true);

    logstashEncoder.setThrowableConverter(throwableConverter);
    logstashAppender.setEncoder(logstashEncoder);
    logstashAppender.start();

    // Wrap the appender in an Async appender for performance
    final AsyncAppender asyncLogstashAppender = new AsyncAppender();
    asyncLogstashAppender.setContext(loggerContext);
    asyncLogstashAppender.setName(LOGSTASH_ASYNC_APPENDER_NAME);
    asyncLogstashAppender.setQueueSize(this.logstash.getQueueSize());
    asyncLogstashAppender.addAppender(logstashAppender);
    asyncLogstashAppender.start();
    loggerContext.getLogger("ROOT").addAppender(asyncLogstashAppender);
  }
}
```

#### 通过定时器模拟随机日志

```console
...
2021-07-25 23:00:36.552  INFO 15928 --- [           main] i.g.y.s.e.config.LogstashLogbackConfig   : Initializing LogstashAppender
2021-07-25 23:00:36.813  INFO 15928 --- [           main] o.s.s.concurrent.ThreadPoolTaskExecutor  : Initializing ExecutorService 'applicationTaskExecutor'
2021-07-25 23:00:37.017  INFO 15928 --- [           main] o.s.s.c.ThreadPoolTaskScheduler          : Initializing ExecutorService 'taskScheduler'
2021-07-25 23:00:37.084  INFO 15928 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
2021-07-25 23:00:37.095  INFO 15928 --- [   scheduling-1] i.g.y.s.elk.scheduler.MockLogScheduler   : [4a2fa762-9390-48f3-8478-4fcdbf6ba017] mock log event, log something...
2021-07-25 23:00:37.097  WARN 15928 --- [   scheduling-1] i.g.y.s.elk.scheduler.MockLogScheduler   : [c859978e-5150-4fe4-979f-9acd7957a55a] mock log event, log something...
2021-07-25 23:00:37.097 ERROR 15928 --- [   scheduling-1] i.g.y.s.elk.scheduler.MockLogScheduler   : [7625e2e6-62e9-4bbf-a998-1e741723f824] mock log event, log something...
2021-07-25 23:00:37.599  INFO 15928 --- [   scheduling-1] i.g.y.s.elk.scheduler.MockLogScheduler   : [54ba65f2-05d7-4f27-ab63-bd19a0165b6b] mock log event, log something...
2021-07-25 23:00:37.599  WARN 15928 --- [   scheduling-1] i.g.y.s.elk.scheduler.MockLogScheduler   : [901ca6a0-fc7b-4749-8661-4c81f7c4701d] mock log event, log something...
2021-07-25 23:00:37.599 ERROR 15928 --- [   scheduling-1] i.g.y.s.elk.scheduler.MockLogScheduler   : [e2010eda-3f63-49ec-bb97-1157a2b11f01] mock log event, log something...
...
```

## 通过 Kibana 管理日志 

### 查看由 Logstash 自动创建的索引

![indices](https://i.loli.net/2021/07/25/2LftsmVuYTkiwKq.png)

### 添加 index 索引

![create index pattern](https://i.loli.net/2021/07/25/DmL7WTOjXiHswA8.png)

![define an index pattern](https://i.loli.net/2021/07/25/Ihud39ps15wX8U2.png)

![configure settings](https://i.loli.net/2021/07/25/Dm9NBCet2u8q45k.png)

### 通过 Discover 查看日志索引信息

![discover logs](https://i.loli.net/2021/07/25/NUw2g57z6WqsG4m.png)

## 参考连接

+ [什么是 ELK Stack？](https://www.elastic.co/cn/what-is/elk-stack)
+ [Invalid kernel settings. Elasticsearch requires at least: vm.max_map_count = 262144](https://github.com/bitnami/bitnami-docker-elasticsearch/issues/61)
+ [芋道 ELK(Elasticsearch + Logstash + Kibana) 极简入门](https://www.iocoder.cn/Elasticsearch/ELK-install/)
