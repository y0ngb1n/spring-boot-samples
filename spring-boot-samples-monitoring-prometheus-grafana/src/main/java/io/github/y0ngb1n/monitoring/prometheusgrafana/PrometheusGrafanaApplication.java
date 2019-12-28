package io.github.y0ngb1n.monitoring.prometheusgrafana;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 使用 Prometheus & Grafana 监控你的 Spring Boot 应用.
 *
 * @author yangbin
 */
@SpringBootApplication
public class PrometheusGrafanaApplication {

  public static void main(String[] args) {
    SpringApplication.run(PrometheusGrafanaApplication.class, args);
  }
}
