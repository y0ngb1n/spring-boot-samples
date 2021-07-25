package io.github.y0ngb1n.samples.elk.scheduler;

import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 模拟系统日志定时器
 *
 * @author yangbin
 */
@Slf4j
@Configuration
@EnableScheduling
public class MockLogScheduler {

  @Scheduled(fixedDelay = 800)
  public void mockLogEvent() {
    final String logTemplate = "[{}] mock log event, log something...";
    log.debug(logTemplate, UUID.randomUUID());
    log.info(logTemplate, UUID.randomUUID());
    log.warn(logTemplate, UUID.randomUUID());
    log.error(logTemplate, UUID.randomUUID());
  }
}
