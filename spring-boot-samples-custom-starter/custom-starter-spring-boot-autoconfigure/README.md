# 定制你的自动配置

> 你的 autoconfigure 模块，包含自己想要定制的自动配置代码。

## Step 1：按需装配

按照自己的需求，配置不同的场景逻辑，只在特定的条件下才注册特定的 Bean，以满足不同的场景。

```java
@Configuration
@ConditionalOnClass(GreetingApplicationRunner.class)
public class CustomStarterAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean(GreetingApplicationRunner.class)
  @ConditionalOnProperty(name = "greeting.enabled", havingValue = "true", matchIfMissing = true)
  public GreetingApplicationRunner greetingApplicationRunner() {
    return new GreetingApplicationRunner();
  }
}
```

## Step 2：启用 Spring 自动装配机制

如果我们直接在其它项目中依赖当前模块，你会发现它并没有工作。这是由于 Spring 并没有加载我们的配置类（也会有例外：在相同的包路径下时），这里还差最后一步，那就是我们只需要按照下面的路径创建一个 `spring.factors` 文件：

`resources/META-INF/spring.factors`

```
# Auto Configure
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
io.github.y0ngb1n.samples.autoconfigure.CustomStarterAutoConfiguration
```

---

## 参考链接

+ https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/
+ https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-developing-auto-configuration.html
+ https://medium.com/@alexeynovikov_89393/how-to-write-your-own-spring-boot-starters-566ce5992954
+ https://github.com/digitalsonic/geektime-spring-family
