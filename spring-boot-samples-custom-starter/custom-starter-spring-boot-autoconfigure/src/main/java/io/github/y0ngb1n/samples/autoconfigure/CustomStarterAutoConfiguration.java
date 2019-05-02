package io.github.y0ngb1n.samples.autoconfigure;

import io.github.y0ngb1n.samples.greeting.GreetingApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 定义自动配置.
 *
 * @author yangbin
 */
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
