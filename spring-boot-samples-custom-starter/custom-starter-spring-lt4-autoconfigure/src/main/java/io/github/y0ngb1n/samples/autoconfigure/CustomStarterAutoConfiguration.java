package io.github.y0ngb1n.samples.autoconfigure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 在 Spring 4 以下版本中定义自动配置.
 *
 * @author yangbin
 */
@Configuration
public class CustomStarterAutoConfiguration {

  /** 如果此处不定义 static，Spring 在启动时会报一个 warning，你可尝试一下. */
  @Bean
  public static GreetingBeanFactoryPostProcessor greetingBeanFactoryPostProcessor() {
    return new GreetingBeanFactoryPostProcessor();
  }
}
