package io.github.y0ngb1n.samples.starter.elasticsearch.core;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.http.HttpHost;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Elasticsearch Client Properties
 *
 * @author yangbin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "elasticsearch")
public class ElasticsearchClientProperties {

  /** 集群节点列表 */
  private List<String> hosts;

  public HttpHost[] buildHttpHosts() {
    return hosts.stream().map(HttpHost::create).toArray(HttpHost[]::new);
  }
}
