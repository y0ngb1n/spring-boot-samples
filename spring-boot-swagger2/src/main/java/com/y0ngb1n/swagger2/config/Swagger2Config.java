package com.y0ngb1n.swagger2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Configuration for Swagger2.
 *
 * @author y0ngb1n
 * @version 1.0.0
 */
@EnableSwagger2
@Configuration
public class Swagger2Config {

  @Bean
  public Docket createRestApi() {
    // @formatter:off
    return new Docket(DocumentationType.SWAGGER_2)
        .apiInfo(apiInfo())
        .select()
          // 指定 Controller 包路径, 不然生成的文档扫描不到接口
          .apis(RequestHandlerSelectors.basePackage("com.y0ngb1n.swagger2.controller"))
          // .paths(PathSelectors.ant("/api/v1/*"))
          .paths(PathSelectors.any())
          .build();
    // @formatter:on
  }

  private ApiInfo apiInfo() {
    return new ApiInfoBuilder()
        .title("XXX 系统 API 服务")
        .description("XXX 系统 API 接口文档简要描述")
        .termsOfServiceUrl("https://github.com/y0ngb1n")
        .version("1.0.0")
        .license("MIT 协议")
        .licenseUrl("http://www.opensource.org/licenses/MIT")
        .contact(new Contact("杨斌", "https://github.com/y0ngb1n", "y0ngb1n.me@gmail.com"))
        .build();
  }
}
