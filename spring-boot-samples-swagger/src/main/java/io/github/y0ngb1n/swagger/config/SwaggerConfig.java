package io.github.y0ngb1n.swagger.config;

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
 * 定义 Swagger 配置.
 *
 * @author y0ngb1n
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
        .apiInfo(apiInfo())
        .select()
        // .apis(RequestHandlerSelectors.any())
        // 指定 Controller 包路径, 不然生成的文档扫描不到接口
        .apis(RequestHandlerSelectors.basePackage("io.github.y0ngb1n.swagger.controller"))
        // .paths(PathSelectors.ant("/api/v1/*"))
        .paths(PathSelectors.any())
        .build();
  }

  private ApiInfo apiInfo() {
    return new ApiInfoBuilder()
        .title("Spring Boot 项目集成 Swagger 实例文档")
        .description("求关注ヾ(◍°∇°◍)ﾉﾞ\n" + "\n" + "GitHub: y0ngb1n\n" + "Blog: y0ngb1n.github.io")
        .termsOfServiceUrl("https://github.com/y0ngb1n")
        .version("v1.0.0")
        .license("MIT 协议")
        .licenseUrl("http://www.opensource.org/licenses/MIT")
        .contact(new Contact("杨斌", "https://github.com/y0ngb1n", "y0ngb1n@163.com"))
        .build();
  }
}
