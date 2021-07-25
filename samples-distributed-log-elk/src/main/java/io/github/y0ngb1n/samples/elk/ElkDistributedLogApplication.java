package io.github.y0ngb1n.samples.elk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 使用 ELK 进行日志的集中管理
 *
 * @author yangbin
 */
@SpringBootApplication
public class ElkDistributedLogApplication {

  public static void main(String[] args) {
    SpringApplication.run(ElkDistributedLogApplication.class, args);
  }
}
