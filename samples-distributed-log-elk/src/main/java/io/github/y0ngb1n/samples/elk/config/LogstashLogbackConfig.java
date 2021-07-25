package io.github.y0ngb1n.samples.elk.config;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.LoggerContext;
import java.net.InetSocketAddress;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.appender.LogstashTcpSocketAppender;
import net.logstash.logback.encoder.LogstashEncoder;
import net.logstash.logback.stacktrace.ShortenedThrowableConverter;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Logstash Logback Config.
 *
 * @author yangbin
 */
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "elk.logstash", name = "enabled", havingValue = "true")
public class LogstashLogbackConfig {

  private static final String LOGSTASH_APPENDER_NAME = "LOGSTASH";
  private static final String LOGSTASH_ASYNC_APPENDER_NAME = "ASYNC_LOGSTASH";

  @Value("${spring.application.name}")
  private String appName;

  @Autowired
  private LogstashProperties logstash;

  @Bean
  @ConfigurationProperties(prefix = "elk.logstash")
  public LogstashProperties logstash() {
    return new LogstashProperties();
  }

  @PostConstruct
  private void addLogstashAppender() {
    LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

    log.info("Initializing LogstashAppender");
    final LogstashTcpSocketAppender logstashAppender = new LogstashTcpSocketAppender();
    logstashAppender.setName(LOGSTASH_APPENDER_NAME);
    logstashAppender.setContext(loggerContext);
    logstashAppender.addDestinations(
      new InetSocketAddress(this.logstash.getHost(), this.logstash.getPort())
    );

    // https://github.com/logstash/logstash-logback-encoder
    final LogstashEncoder logstashEncoder = new LogstashEncoder();
    logstashEncoder.setIncludeContext(false);
    String customFields = "{\"app_name\":\"" + this.appName +"\",\"idol\":\"yangbin\"}";
    logstashEncoder.setCustomFields(customFields);

    final ShortenedThrowableConverter throwableConverter = new ShortenedThrowableConverter();
    throwableConverter.setRootCauseFirst(true);

    logstashEncoder.setThrowableConverter(throwableConverter);
    logstashAppender.setEncoder(logstashEncoder);
    logstashAppender.start();

    // Wrap the appender in an Async appender for performance
    final AsyncAppender asyncLogstashAppender = new AsyncAppender();
    asyncLogstashAppender.setContext(loggerContext);
    asyncLogstashAppender.setName(LOGSTASH_ASYNC_APPENDER_NAME);
    asyncLogstashAppender.setQueueSize(this.logstash.getQueueSize());
    asyncLogstashAppender.addAppender(logstashAppender);
    asyncLogstashAppender.start();
    loggerContext.getLogger("ROOT").addAppender(asyncLogstashAppender);
  }
}
