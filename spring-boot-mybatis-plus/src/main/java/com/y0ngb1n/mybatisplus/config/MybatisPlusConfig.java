package com.y0ngb1n.mybatisplus.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Configuration for Mybatis-Plus.
 *
 * @author y0ngb1n
 * @version 1.0.0
 */
@EnableTransactionManagement
@MapperScan(basePackages = "com.y0ngb1n.mybatisplus.mapper")
@Configuration
public class MybatisPlusConfig {

  /**
   * 分页插件.
   */
  @Bean
  public PaginationInterceptor paginationInterceptor() {
    return new PaginationInterceptor();
  }
}
