# 利用 Spring Boot & Redis 实现短链接服务

## 准备工作

+ Spring Boot 2.1.0+
+ Redis
+ Lombok
+ Guava 28.0
+ Common Validator 1.6

## 添加依赖项

`pom.xml`
```xml
<dependencies>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>

    <dependency>
      <groupId>commons-validator</groupId>
      <artifactId>commons-validator</artifactId>
    </dependency>

    <dependency>
      <groupId>com.google.guava</groupId>
      <artifactId>guava</artifactId>
    </dependency>
</dependencies>
```

`application.yml`
```yaml
spring:
  # Redis Config
  redis:
    url: 127.0.0.1
    port: 6379
    password: your_password

logging:
  level:
    io.github.y0ngb1n.*: debug
```

## 核心代码

```diff
/**
 * URL Shortener Resource
 *
 * @author yangbin
 */
@Slf4j
@RestController
@RequestMapping(path = "/v1")
public class UrlShortenerController {

  @Autowired
  StringRedisTemplate redisTemplate;

  @GetMapping(path = "/{id}")
  public String getUrl(@PathVariable String id) {
+   String url = redisTemplate.opsForValue().get(id);
    log.debug("URL Retrieved: {}", url);
    return url;
  }

  @PostMapping
  public String create(@RequestBody String url) {
    UrlValidator urlValidator = new UrlValidator(
      new String[]{"http", "https"}
    );
    if (urlValidator.isValid(url)) {
-     String id = Hashing.murmur3_32().hashString(url, StandardCharsets.UTF_8).toString();
      log.debug("URL Id generated: {}", id);
+     redisTemplate.opsForValue().set(id, url);
      return id;
    }
    throw new RuntimeException("URL Invalid: " + url);
  }
}
```

## 使用方式

**Step 0: 安装并启动 Redis**

```bash
# on Windows
scoop install redis
redis-server

# on Mac
brew install redis
redis-server
```

**Step 1: 启动 `url-shortener` 服务**

```console
$ mvn install
...
[INFO] BUILD SUCCESS
...
$ mvn spring-boot:run
...
2019-08-21 21:03:50.215  INFO 10244 --- [ main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
2019-08-21 21:03:50.219  INFO 10244 --- [ main] i.g.y.s.u.UrlShortenerApplication        : Started UrlShortenerApplication in 6.01 seconds (JVM running for 12.165)
```

**Step 2: 生成短链**

```console
$ curl -X POST http://127.0.0.1:8080/v1 \
  -H 'Content-Type: text/plain' \
  -d https://y0ngb1n.github.io
515bbe2b
```

**Step 3: 还原短链**

```console
$ curl -X GET http://127.0.0.1:8080/v1/515bbe2b
https://y0ngb1n.github.io
```

查看日志

```console
...
2019-08-21 21:42:26.788 DEBUG 10244 --- [nio-8080-exec-2] i.g.y.s.u.c.UrlShortenerController       : URL Id generated: 515bbe2b
2019-08-21 21:42:40.748 DEBUG 10244 --- [nio-8080-exec-3] i.g.y.s.u.c.UrlShortenerController       : URL Retrieved: https://y0ngb1n.github.io
```

## 参考资料

+ https://youtu.be/Zr0E2VP24w8
+ https://en.wikipedia.org/wiki/MurmurHash
+ https://github.com/google/guava/wiki/HashingExplained
+ https://www.flyml.net/2016/09/05/cassandra-tutorial-murmurhash/
+ [短网址（Short URL）系统的原理及其实现](https://hufangyun.com/2017/short-url/)，by 胡方运
+ [如何快速判断某 URL 是否在一个 20 亿的网址 URL 集合中？](https://zhangzw.com/20190521.html)，by 张振伟
  - 应用场景：`黑名单`、`URL 去重`、`单词拼写检查`、`Key-Value 缓存系统的 Key 校验`、`ID 校验，比如订单系统查询某个订单 ID 是否存在，如果不存在就直接返回`
+ [谈谈全局唯一 ID 生成方法](https://yuerblog.cc/2017/06/06/unique-id-generator/)
+ [Leaf — 美团点评分布式 ID 生成系统](https://tech.meituan.com/2017/04/21/mt-leaf.html)
