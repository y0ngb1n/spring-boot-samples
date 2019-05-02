# 如何在低版本的 Spring 中快速实现类似自动配置的功能

> 在 Spring 4 后才引入了 `@Conditional` 等条件注解，它是 Spring Boot 中实现自动配置的最大功臣！\
那么问题来了：**如果我们还在使用 Spring 3.x 的老版本，这时候要怎么实现一个自动配置呢？**

## 需求和问题

### 核心的诉求

+ 现存系统，不打算重构
+ Spring 版本为 3.x，也不打算升级版本和引入 Spring Boot
+ 期望能够在少改代码的前提下实现功能增强
  
比如说：

+ 希望能够给全站统一添加上日志记录（如：RPC 框架 Web 调用的摘要信息、数据库访问层的摘要信息），这个其实是个通用的功能。
+ 我们引用了一些基础设施，并想对这些基础设施的功能作进一步的增强，这时候就应该从框架的层面来解决这个问题。

### 面临的问题

+ 3.x 的 Spring 没有条件注解

  因为没有条件注解，所以我们不清楚在什么时候 需要/不需要 配置这些东西

+ 无法自动定位需要加载的自动配置

  此时我们没有办法像 Spring Boot 的自动配置那样让框架自动加载我们的配置，我们要使用一些别的手段让 Spring 可以加载到我们定制的这些功能。
  
## 核心解决思路

### 条件判断

+ 通过 `BeanFactoryPostProcessor` 进行判断

Spring 为我们提供了一个扩展点，我们可以通过 `BeanFactoryPostProcessor` 来解决条件判断的问题，它可以让我们在 `BeanFactory` 定义完之后、Bean 的初始化之前对我们这些 Bean 的定义做一些后置的处理。可以在这个时候对我们的 Bean 定义做判断，看看当前 存在/缺少 哪些 Bean 的定义，还可以增加一些 Bean 的定义 —— 加入一些自己定制的 Bean。

### 配置加载

+ 编写 Java Config 类
+ 引入配置类
  + 通过 `component-scan`
  + 通过 XML 文件 `import`

可以考虑编写自己的 Java Config 类，并把它加到 `component-scan` 里面，然后想办法让现在系统的 `component-scan` 包含我们编写的 Java Config 类；也可以编写 XML 文件，如果当前系统使用 XML 的方式，那么它加载的路径上是否可以加载我们的 XML 文件，如果不行就可以使用手动 `import` 这个文件。

## Spring 提供的两个扩展点

### `BeanPostProcessor`

+ 针对 Bean 实例
+ 在 Bean 创建后提供定制逻辑回调

### `BeanFactoryPostProcessor`

+ 针对 Bean 定义
+ 在容器创建 Bean 前获取配置元数据
+ Java Config 中需要定义为 `static` 方法（如果不定义，Spring 在启动时会报一个 `warning`，你可尝试一下）

## 关于 Bean 的一些定制

> 既然上面提到了 Spring 的两个扩展点，这里就延展一下关于 Bean 的一些定制的方式。

### Lifecycle Callback

+ `InitializingBean` / `@PostConstruct` / `init-method`

  这部分是关于初始化的，可以在 Bean 的初始化之后做一些定制，这里有三种方式：
  
  + 实现 `InitializingBean` 接口
  + 使用 `@PostConstruct` 注解
  + 在 Bean 定义的 XML 文件里给它指定一个 `init-method`；亦或者在使用 `@Bean` 注解时指定 `init-method`
  
  这些都可以让我们这个 Bean 在创建之后去调用特定的方法。

+ `DisposableBean` / `@PreDestroy` / `destroy-method`

  这部分是在 Bean 回收的时候，我们该做的一些操作。可以指定这个 Bean 在销毁的时候，如果：
  
  + 它实现了 `DisposableBean` 这个接口，那么 Spring 会去调用它相应的方法
  + 也可以将 `@PreDestroy` 注解加在某个方法上，那么会在销毁时调用这个方法
  + 在 Bean 定义的 XML 文件里给它指定一个 `destroy-method`；亦或者在使用 `@Bean` 注解时指定 `destroy-method`，那么会在销毁时调用这个方法

### XxxAware 接口

+ `ApplicationContextAware`

  可以把整个 `ApplicationContext` 通过接口进行注入，在这个 Bean 里我们就可以获得一个完整的 `ApplicationContext`。

+ `BeanFactoryAware`

  与 `ApplicationContextAware` 类似。

+ `BeanNameAware`

  可以把 Bean 的名字注入到这个实例中来。
  
> 如果对源码感兴趣，可见：`org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean`\
如果当前 Bean 存在 `close` 或 `shutdown` 方法名的方法时，会被 Spring 视为 `destroy-method`，在销毁时会进行调用。

## 一些常用操作

### 判断类是否存在

+ `ClassUitls.isPresent()`

调用 Spring 提供的 `ClassUitls.isPresent()` 来判断一个类是否存在当前 Class Path 下。

### 判断 Bean 是否已定义

+ `ListableBeanFactory.containsBeanDefinition()`：判断 Bean 是否已定义。
+ `ListableBeanFactory.getBeanNamesForType()`：可以查看某些类型的 Bean 都有哪些名字已经被定义了。

### 注册 Bean 定义

+ `BeanDefinitionRegistry.registerBeanDefinition()`
  + `GenericBeanDefinition`
+ `BeanFactory.registerSingleton()`

## 撸起袖子加油干

> 理论就科普完了，下面就开始实践。\
在当前的例子中，我们假定一下当前环境为：**没有使用 Spring Boot 以及高版本的 Spring**。

**Step 1：模拟低版本的 Spring 环境**

这里只是简单地引入了 `spring-context` 依赖，并没有真正的使用 Spring 3.x 的版本，但也没有使用 Spring 4 以上的一些特性。

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

**Step 2：以实现 `BeanFactoryPostProcessor` 接口为例**

```java
@Slf4j
public class GreetingBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

  @Override
  public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
    throws BeansException {

    // 判断当前 Class Path 下是否存在所需要的 GreetingApplicationRunner 这么一个类
    boolean hasClass = ClassUtils
      .isPresent("io.github.y0ngb1n.samples.greeting.GreetingApplicationRunner",
        GreetingBeanFactoryPostProcessor.class.getClassLoader());

    if (!hasClass) {
      // 类不存在
      log.info("GreetingApplicationRunner is NOT present in CLASSPATH.");
      return;
    }

    // 是否存在 id 为 greetingApplicationRunner 的 Bean 定义
    boolean hasDefinition = beanFactory.containsBeanDefinition("greetingApplicationRunner");
    if (hasDefinition) {
      // 当前上下文已存在 greetingApplicationRunner
      log.info("We already have a greetingApplicationRunner bean registered.");
      return;
    }
    
    register(beanFactory);
  }

  private void register(ConfigurableListableBeanFactory beanFactory) {

    if (beanFactory instanceof BeanDefinitionRegistry) {
      GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
      beanDefinition.setBeanClass(GreetingApplicationRunner.class);

      ((BeanDefinitionRegistry) beanFactory)
        .registerBeanDefinition("greetingApplicationRunner", beanDefinition);
    } else {

      beanFactory.registerSingleton("greetingApplicationRunner", new GreetingApplicationRunner());
    }
  }
}
```

注册我们的 Bean（见 [`CustomStarterAutoConfiguration`](./src/main/java/io/github/y0ngb1n/samples/autoconfigure/CustomStarterAutoConfiguration.java)），如下有几点是需要注意的：

+ 这里的方法定义为 `static`
+ 使用时，如果两项目不是在同个包下，需要主动将当前类加入到项目的 `component-scan` 里

```java
@Configuration
public class CustomStarterAutoConfiguration {

  @Bean
  public static GreetingBeanFactoryPostProcessor greetingBeanFactoryPostProcessor() {
    return new GreetingBeanFactoryPostProcessor();
  }
}
```

**Step 3：验证该自动配置是否生效**

在其他项目中添加依赖：

```xml
<dependencies>
  ...
  <dependency>
    <groupId>io.github.y0ngb1n.samples</groupId>
    <artifactId>custom-starter-spring-lt4-autoconfigure</artifactId>
  </dependency>
  <dependency>
    <groupId>io.github.y0ngb1n.samples</groupId>
    <artifactId>custom-starter-core</artifactId>
  </dependency>
  ...
</dependencies>
```

启动项目并观察日志（见 [`custom-starter-examples`](../custom-starter-examples)），验证自动配置是否生效了：

```console
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::        (v2.1.0.RELEASE)

2019-05-02 20:47:27.692  INFO 11460 --- [           main] i.g.y.s.d.AutoconfigureDemoApplication   : Starting AutoconfigureDemoApplication on HP with PID 11460 ...
2019-05-02 20:47:27.704  INFO 11460 --- [           main] i.g.y.s.d.AutoconfigureDemoApplication   : No active profile set, falling back to default profiles: default
2019-05-02 20:47:29.558  INFO 11460 --- [           main] i.g.y.s.g.GreetingApplicationRunner      : Initializing GreetingApplicationRunner.
2019-05-02 20:47:29.577  INFO 11460 --- [           main] i.g.y.s.d.AutoconfigureDemoApplication   : Started AutoconfigureDemoApplication in 3.951 seconds (JVM running for 14.351)
2019-05-02 20:47:29.578  INFO 11460 --- [           main] i.g.y.s.g.GreetingApplicationRunner      : Hello everyone! We all like Spring!
```

到这里，已成功在低版本的 Spring 中实现了类似自动配置的功能。:clap:

---

## 参考链接

+ https://github.com/digitalsonic/geektime-spring-family
