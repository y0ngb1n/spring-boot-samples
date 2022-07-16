package io.github.y0ngb1n.samples.starter.elasticsearch.config;

import io.github.y0ngb1n.samples.starter.elasticsearch.core.ElasticsearchClientProperties;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Elasticsearch Client 配置类
 *
 * @author yangbin
 */
@RequiredArgsConstructor
@Configuration
@Import({ElasticsearchClientProperties.class})
public class ElasticsearchClientConfig {

  private final ElasticsearchClientProperties clientProperties;

  @Bean
  @ConditionalOnMissingBean(name = "elasticsearchRestHighLevelClient")
  public RestHighLevelClient elasticsearchRestHighLevelClient() {
    final RestClientBuilder clientBuilder = RestClient.builder(clientProperties.buildHttpHosts());
    return new RestHighLevelClient(clientBuilder);
  }
}
