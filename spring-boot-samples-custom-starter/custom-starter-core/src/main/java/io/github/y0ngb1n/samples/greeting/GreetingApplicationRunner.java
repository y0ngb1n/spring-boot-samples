package io.github.y0ngb1n.samples.greeting;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

/**
 * @author yangbin
 */
@Slf4j
public class GreetingApplicationRunner implements ApplicationRunner {

  public GreetingApplicationRunner() {
    log.info("Initializing GreetingApplicationRunner.");
  }

  @Override
  public void run(ApplicationArguments args) throws Exception {
    log.info("Hello everyone! We all like Spring!");
  }
}
