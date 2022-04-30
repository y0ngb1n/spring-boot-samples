package io.github.y0ngb1n.samples.elk.config;

import lombok.Data;

/** @author yangbin */
@Data
public class LogstashProperties {

  private boolean enabled;

  private String host = "localhost";

  private int port = 4560;

  private int queueSize = 125;
}
