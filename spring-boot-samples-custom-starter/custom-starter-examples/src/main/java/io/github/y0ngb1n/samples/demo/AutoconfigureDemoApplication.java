package io.github.y0ngb1n.samples.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Autoconfigure Demo.
 *
 * @author yangbin
 */
@SpringBootApplication(scanBasePackages = "io.github.y0ngb1n.samples")
public class AutoconfigureDemoApplication {

  public static void main(String[] args) {
    SpringApplication.run(AutoconfigureDemoApplication.class, args);
  }
}
