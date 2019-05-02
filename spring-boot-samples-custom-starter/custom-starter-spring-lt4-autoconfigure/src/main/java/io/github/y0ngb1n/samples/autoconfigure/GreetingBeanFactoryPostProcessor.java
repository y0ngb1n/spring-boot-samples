package io.github.y0ngb1n.samples.autoconfigure;

import io.github.y0ngb1n.samples.greeting.GreetingApplicationRunner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.util.ClassUtils;

/**
 * @author yangbin
 */
@Slf4j
public class GreetingBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

  private static final String GREETING_BEAN_NAME = "greetingApplicationRunner";

  @Override
  public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {

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
    boolean hasDefinition = beanFactory.containsBeanDefinition(GREETING_BEAN_NAME);
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
        .registerBeanDefinition(GREETING_BEAN_NAME, beanDefinition);
    } else {

      beanFactory.registerSingleton(GREETING_BEAN_NAME, new GreetingApplicationRunner());
    }
  }
}
