package io.github.y0ngb1n.samples.datasource.druid.config;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import javax.sql.DataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * Druid 配置类.
 *
 * @author yangbin
 */
@Configuration
@Profile({"multi"})
public class DruidConfig {

  @Primary
  @Bean
  @ConfigurationProperties("spring.datasource.druid.foo")
  public DataSource dataSourceFoo() {
    return DruidDataSourceBuilder.create().build();
  }

  @Bean
  @ConfigurationProperties("spring.datasource.druid.bar")
  public DataSource dataSourceBar() {
    return DruidDataSourceBuilder.create().build();
  }
}
