## Spring Boot 配置 Swagger2 接口文档引擎

### Maven
增加 Swagger2 所需依赖，pom.xml 配置如下：

```xml
<swagger2.version>2.9.2</swagger2.version>

<!-- Swagger2 Begin -->
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger2</artifactId>
      <version>${swagger2.version}</version>
</dependency>
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger-ui</artifactId>
      <version>${swagger2.version}</version>
</dependency>
<!-- Swagger2 End -->
```

### 配置 Swagger2

> 参考 `Swagger2Config.java`

### 启用 Swagger2
在 Application 或配置类中加上注解 `@EnableSwagger2` 表示开启 Swagger

### Swagger 注解说明
Swagger 通过注解表明该接口会生成文档，包括接口名、请求方法、参数、返回信息的等等。

+ `@Api`：修饰整个类，描述 Controller 的作用
+ `@ApiOperation`：描述一个类的一个方法，或者说一个接口
+ `@ApiParam`：单个参数描述
+ `@ApiModel`：用对象来接收参数
+ `@ApiProperty`：用对象接收参数时，描述对象的一个字段
+ `@ApiResponse`：HTTP 响应其中 1 个描述
+ `@ApiResponses`：HTTP 响应整体描述
+ `@ApiIgnore`：使用该注解忽略这个API
+ `@ApiImplicitParam`：一个请求参数
+ `@ApiImplicitParams`：多个请求参数

以上这些就是最常用的几个注解了。

具体其他的注解，查看：https://github.com/swagger-api/swagger-core/wiki/Annotations#apimodel

更多请参考 [Swagger 注解文档](http://docs.swagger.io/swagger-core/apidocs/com/wordnik/swagger/annotations/package-summary.html)

### 访问 Swagger2
访问地址：http://ip:port/swagger-ui.html

![文档演示](https://i.loli.net/2018/11/19/5bf2ac995fed6.png)