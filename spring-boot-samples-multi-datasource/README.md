# Spring Boot 配置多数据源

## 注意事项

**不同数据源的配置要分开**

在 Spring 中，我们需要将两个不同数据源的配置分开。如我们有一个 `foo-datasource` 和另一个 `bar-datasource`，那将这两个 DataSource 的所有配置都要分开，不要将它们放在一起，就算它们除了 `url` 以外其它配置都相同，也建议你将它们分开配置，这样会方便我们以后的配置管理。

**关注每次使用的数据源**

+ 有多个 DataSource 时系统如何判断
+ 对应的设施（事务、ORM 等）如何选择 DataSource

在编程的时候，我们要格外地注意当前在操作哪一个数据源，要告诉我们的系统要使用哪一个数据源，事务管理应该启在哪个数据源上面；同时地我们也要告诉相关 ORM（像 Hibernate、MyBatis 类似的设施）此时是在操作哪一个数据源，因为这些框架并没有帮我们考虑得这么周全，所以在编码时一定要格外地小心。

## 多数据源配置

**手工配置两组 DataSource 及相关内容**

如果我们完全地靠手工的配置，排除掉整个 Spring Boot 相关的依赖，全部都交由自己来配置肯定是可以的。

如果还是想与 Spring Boot 结合在一起的话可以参考下面的两种方式。

**与 Spring Boot 协同工作（二选一）**

+ 方式一：配置 `@Primary` 类型的 Bean
+ 方式二：排除 Spring Boot 的自动配置
  + `DataSourceAutoConfiguration`
  + `DataSourceTransactionManagerAutoConfiguration`
  + `JdbcTemplateAutoConfiguration`
  
**方式一**：Spring 会把配置了 `@Primary` 的 Bean 作为主要的 Bean，在后面 Spring Boot 相关的自动配置都会环绕这个标志了 `@Primary` 的 DataSource 进行配置。

**方式二**：如果你觉得这两个 Bean 是没有主次之分、这两个 DataSource 都同等重要的，那么我们可以排除掉方式二所列举的 Bean。把它们排除掉之后，我们可以在代码中自己来控制它，可参考如下代码：

Step 01：排除 Spring Boot 的自动配置
```java
@SpringBootApplication(exclude = {
  DataSourceAutoConfiguration.class,
  DataSourceTransactionManagerAutoConfiguration.class,
  JdbcTemplateAutoConfiguration.class
})
public class MultiDataSourceApplication {

  public static void main(String[] args) {
    SpringApplication.run(MultiDataSourceApplication.class, args);
  }
}
```

Step 02：添加 `foo`、`bar` 数据源配置

```yaml
# Foo DataSource
foo:
  datasource:
    url: jdbc:h2:mem:foo
    username: sa
    password:

# Bar DataSource
bar:
  datasource:
    url: jdbc:h2:mem:bar
    username: sa
    password:
```

Step 03: 分别为 `foo-datasource`、`bar-datasource` 添加配置，及对应的事务管理器

```java
@Slf4j
@Configuration
public class MultiDataSourceConfig {

  //---------------------------------------------------------------------
  // Config Foo DataSource
  //---------------------------------------------------------------------

  @Bean
  @ConfigurationProperties("foo.datasource")
  public DataSourceProperties fooDataSourceProperties() {
    return new DataSourceProperties();
  }

  @Bean
  public DataSource fooDataSource() {
    DataSourceProperties dataSourceProperties = fooDataSourceProperties();
    log.info("foo datasource: {}", dataSourceProperties.getUrl());
    return dataSourceProperties.initializeDataSourceBuilder().build();
  }

  @Bean
  @Resource
  public PlatformTransactionManager fooTxManager(DataSource fooDataSource) {
    return new DataSourceTransactionManager(fooDataSource);
  }
  
  //---------------------------------------------------------------------
  // Config Bar DataSource
  //---------------------------------------------------------------------

  @Bean
  @ConfigurationProperties("bar.datasource")
  public DataSourceProperties barDataSourceProperties() {
    return new DataSourceProperties();
  }

  @Bean
  public DataSource barDataSource() {
    DataSourceProperties dataSourceProperties = barDataSourceProperties();
    log.info("bar datasource: {}", dataSourceProperties.getUrl());
    return dataSourceProperties.initializeDataSourceBuilder().build();
  }

  @Bean
  @Resource
  public PlatformTransactionManager barTxManager(DataSource barDataSource) {
    return new DataSourceTransactionManager(barDataSource);
  }
}
```

Step 04: 检验是否配置成功

```console
$ mvn spring-boot:run
...
2019-09-18 23:57:21.649  INFO 15826 --- [           main] i.g.y.s.m.config.MultiDataSourceConfig   : foo datasource: jdbc:h2:mem:foo
2019-09-18 23:57:21.708  INFO 15826 --- [           main] i.g.y.s.m.config.MultiDataSourceConfig   : bar datasource: jdbc:h2:mem:bar
...
$ curl -s http://localhost:8080/actuator/beans | jq
{
  "contexts": {
    "application": {
      "beans": {

        ...
        "fooDataSourceProperties": {
          "aliases": [],
          "scope": "singleton",
          "type": "org.springframework.boot.autoconfigure.jdbc.DataSourceProperties",
          "resource": "class path resource [io/github/y0ngb1n/samples/multidatasource/config/MultiDataSourceConfig.class]",
          "dependencies": []
        },
        "fooDataSource": {
          "aliases": [],
          "scope": "singleton",
          "type": "com.zaxxer.hikari.HikariDataSource",
          "resource": "class path resource [io/github/y0ngb1n/samples/multidatasource/config/MultiDataSourceConfig.class]",
          "dependencies": [
            "fooDataSourceProperties"
          ]
        },
        "fooTxManager": {
          "aliases": [],
          "scope": "singleton",
          "type": "org.springframework.jdbc.datasource.DataSourceTransactionManager",
          "resource": "class path resource [io/github/y0ngb1n/samples/multidatasource/config/MultiDataSourceConfig.class]",
          "dependencies": [
            "fooDataSource"
          ]
        },

        ...
        "barDataSourceProperties": {
          "aliases": [],
          "scope": "singleton",
          "type": "org.springframework.boot.autoconfigure.jdbc.DataSourceProperties",
          "resource": "class path resource [io/github/y0ngb1n/samples/multidatasource/config/MultiDataSourceConfig.class]",
          "dependencies": []
        },
        "barDataSource": {
          "aliases": [],
          "scope": "singleton",
          "type": "com.zaxxer.hikari.HikariDataSource",
          "resource": "class path resource [io/github/y0ngb1n/samples/multidatasource/config/MultiDataSourceConfig.class]",
          "dependencies": [
            "barDataSourceProperties"
          ]
        },
        "barTxManager": {
          "aliases": [],
          "scope": "singleton",
          "type": "org.springframework.jdbc.datasource.DataSourceTransactionManager",
          "resource": "class path resource [io/github/y0ngb1n/samples/multidatasource/config/MultiDataSourceConfig.class]",
          "dependencies": [
            "barDataSource"
          ]
        },

        ...
        "multiDataSourceConfig": {
          "aliases": [],
          "scope": "singleton",
          "type": "io.github.y0ngb1n.samples.multidatasource.config.MultiDataSourceConfig$$EnhancerBySpringCGLIB$$63c24bed",
          "resource": "file [/Users/yangbin/workspace/coding/Java/spring-boot-samples/spring-boot-samples-multi-datasource/target/classes/io/github/y0ngb1n/samples/multidatasource/config/MultiDataSourceConfig.class]",
          "dependencies": []
        },
        ...
      },
      "parentId": null
    }
  }
}
```

上面可以查看到 Spring 中各 Bean 的依赖关系。

这样就完成了 Spring Boot 中多数据源的配置。如果我们还有更多的数据源的话，配置也是类似的，参考上面的配置即可。

---

## 参考链接
