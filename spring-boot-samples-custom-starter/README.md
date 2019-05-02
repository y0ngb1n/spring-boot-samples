# 如何定制自己的起步依赖

> 在这里我们一起动手实现一个属于自己的起步依赖

## 主要内容

主要加入两个模块，一个是与自动配置相关的模块，如果你的依赖需要做自动配置，那么我们可以在里面写上自动配置。另一个是 `starter` 模块，它里面就是一些依赖项，首先就是指向我们的 `autoconfigure` 模块的一个依赖，另外就是当前这个 Starter 所自己需要的依赖项。

+ `autoconfigure` 模块，包含自动配置代码
+ `starter` 模块，包含指向自动配置模块的依赖及其他相关依赖

这里说明一下，`autoconfigure` 并不是必须的，如果当前这个模块并不需要什么自动配置，就可以把它去掉。而 Spring Boot 相关的那些自动配置很多都是集中在 `spring-boot-autoconfigure` 里面的，所以它只要依赖了 `spring-boot-starter`，那么就会自动地加入这些 Autoconfigure。

## 命名方式

一般是建议在前面加上一个前缀，主要是与 Spring Boot 官方的那些依赖做区分，如下所示：

+ xxx-spring-boot-autoconfigure
+ xxx-spring-boot-starter

这样就可以定义一个你自己的 Spring Boot Starter 了。

## 一些注意事项

+ **不要使用 `spring-boot` 作为依赖的前缀**

  如果你这样做了，会和 Spring Boot 官方的那些依赖混在一起，从而导致不好辨认。

+ **不要使用 `spring-boot` 的配置命名空间**

  另外如果你有一些配置，这里也是建议不要使用 Spring Boot 已经在使用的配置命名空间。比方说它里面有 `server`、`management` 相关的这些配置项，那么你就不要再使用以 `server`、`management` 命名前缀的配置了。

+ `starter` 中仅添加必要的依赖

  在当前这个 Starter 中只加入必要的依赖就可以了。也许这个要求有点苛克，但这里的建议是希望你的依赖不多不少、正正好好，你要使用到哪些依赖就只加这些依赖就好；如果没有必要加进去的就可以去掉它，这样的好处是可以减少最终打出来的包里面的依赖。

+ 声明对 `spring-boot-starter` 的依赖

  如果有需要的可以在这个 Starter 当中加入 `spring-boot-starter` 这个依赖。这个并不是必须的，因为我们现在很多的工程本身就是 `spring-boot` 的一个项目，所以它本身就添加了对 `spring-boot-starter` 的依赖。这个要看你的需要来决定一下是否要添加。

---

## 撸起袖子加油干

下面我们来看看都有哪些方式可以实现自动配置

+ **传统手工实现的自动配置**（见 [`custom-starter-spring-lt4-autoconfigure`](./custom-starter-spring-lt4-autoconfigure)）

  > **注**：在低版本的 Spring 中能使用这种方式快速实现类似自动配置的功能。

  ```xml
  <dependencies>
    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-context</artifactId>
    </dependency>
    <dependency>
      <groupId>org.projectlombok</groupId>
      <artifactId>lombok</artifactId>
    </dependency>
    <dependency>
      <groupId>io.github.y0ngb1n.samples</groupId>
      <artifactId>custom-starter-core</artifactId>
      <scope>provided</scope>
    </dependency>
  </dependencies>
  ```

+ **基于 Spring Boot 的自动配置**（见 [`custom-starter-spring-boot-autoconfigure`](./custom-starter-spring-boot-autoconfigure)）

  ```xml
  <dependencies>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-autoconfigure</artifactId>
    </dependency>
    <dependency>
      <groupId>io.github.y0ngb1n.samples</groupId>
      <artifactId>custom-starter-core</artifactId>
      <scope>provided</scope>
    </dependency>
  </dependencies>
  ```

+ **引用自定义 Starter**（见 [`custom-starter-spring-boot-starter`](./custom-starter-spring-boot-starter)）

  ```xml
  <dependencies>
    <dependency>
      <groupId>io.github.y0ngb1n.samples</groupId>
      <artifactId>custom-starter-spring-boot-starter</artifactId>
    </dependency>
  </dependencies>
  ```

运行 [`custom-starter-examples`](./custom-starter-examples) 效果如下：

```console
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::        (v2.1.0.RELEASE)

2019-05-02 23:15:56.183  INFO 17236 --- [           main] i.g.y.s.d.AutoconfigureDemoApplication   : Starting AutoconfigureDemoApplication on HP with PID 17236 ...
2019-05-02 23:15:56.208  INFO 17236 --- [           main] i.g.y.s.d.AutoconfigureDemoApplication   : No active profile set, falling back to default profiles: default
2019-05-02 23:15:57.198  INFO 17236 --- [           main] i.g.y.s.g.GreetingApplicationRunner      : Initializing GreetingApplicationRunner.
2019-05-02 23:15:57.478  INFO 17236 --- [           main] i.g.y.s.d.AutoconfigureDemoApplication   : Started AutoconfigureDemoApplication in 2.516 seconds (JVM running for 5.501)
2019-05-02 23:15:57.486  INFO 17236 --- [           main] i.g.y.s.g.GreetingApplicationRunner      : Hello everyone! We all like Spring!
```

以上就是一个简单的 Starter，在里面加入自己的自动配置和相关的依赖。那么到这里你也可以实现一个属于你的 Starter，从而简化你的 Maven 依赖项。

---

## 参考链接

+ https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/
+ https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-developing-auto-configuration.html
+ https://medium.com/@alexeynovikov_89393/how-to-write-your-own-spring-boot-starters-566ce5992954
+ https://github.com/digitalsonic/geektime-spring-family
+ https://github.com/marcosbarbero/spring-cloud-zuul-ratelimit
+ https://github.com/biezhi/keeper
