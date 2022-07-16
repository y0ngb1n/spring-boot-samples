package io.github.y0ngb1n.samples.elasticsearch.fetcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * 随机获取一句毒鸡汤 https://github.com/roojay520/cf-worker-soul-soup
 *
 * @author yangbin
 */
@Slf4j
@Component
public class SoulSoupDataFetcher implements AutoCloseable {

  public static final String SOUL_SOUP_INDEX_NAME = "soul_soup";
  public static final SoulSoup DEFAULT_SOUL_SOUP_DOC =
      new SoulSoup(
          666L,
          "全文搜索属于最常见的需求，开源的 Elasticsearch（以下简称 Elastic）是目前全文搜索引擎的首选。它可以快速地储存、搜索和分析海量数据。维基百科、Stack Overflow、Github 都采用它。Elastic 的底层是开源库 Lucene。但是，你没法直接用 Lucene，必须自己写代码去调用它的接口。Elastic 是 Lucene 的封装，提供了 REST API 的操作接口，开箱即用。");;
  private static final String SOUL_SOUP_URL = "https://soul-soup.fe.workers.dev/";

  private final ObjectMapper objectMapper;
  private final RestHighLevelClient restHighLevelClient;
  private final ScheduledExecutorService scheduledExecutorService =
      Executors.newSingleThreadScheduledExecutor();

  public SoulSoupDataFetcher(ObjectMapper objectMapper, RestHighLevelClient restHighLevelClient) {
    this.objectMapper = objectMapper;
    this.restHighLevelClient = restHighLevelClient;
    scheduledExecutorService.scheduleAtFixedRate(this::fetchSoulSoup, 5, 30, TimeUnit.SECONDS);
  }

  @PostConstruct
  public void initialize() throws IOException {
    this.deleteIndex(SOUL_SOUP_INDEX_NAME);
    this.createIndex(SOUL_SOUP_INDEX_NAME);
    updateIndex(DEFAULT_SOUL_SOUP_DOC);
  }

  /** 创建索引库 */
  private void createIndex(String indexName) throws IOException {
    final CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName);
    createIndexRequest.settings(
        Settings.builder()
            // 分片数
            .put("index.number_of_shards", 5)
            // 副本数
            .put("index.number_of_replicas", 1));
    createIndexRequest.mapping(
        "{\n"
            + "  \"properties\": {\n"
            + "    \"id\": {\n"
            + "      \"type\": \"long\"\n"
            + "    },\n"
            + "    \"title\": {\n"
            + "      \"type\": \"text\"\n"
            + "    }\n"
            + "  }\n"
            + "}",
        XContentType.JSON);
    final CreateIndexResponse response =
        restHighLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
    log.info("created index:{}, response:{}.", indexName, response.isAcknowledged());
  }

  /** 删除索引库 */
  private void deleteIndex(String indexName) throws IOException {
    final GetIndexRequest getIndexRequest = new GetIndexRequest(indexName);
    final boolean indexExists =
        restHighLevelClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
    if (!indexExists) {
      log.warn("index:{} not found, skip delete.", indexName);
      return;
    }

    final DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(indexName);
    try {
      final AcknowledgedResponse response =
          restHighLevelClient.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
      log.info("deleted index:{}, response:{}.", indexName, response.isAcknowledged());
    } catch (RuntimeException e) {
      log.error("delete index:{} error.", indexName, e);
    }
  }

  private void fetchSoulSoup() {
    OkHttpClient client = new OkHttpClient().newBuilder().build();
    Request request = new Request.Builder().url(SOUL_SOUP_URL).method("GET", null).build();
    try (Response response = client.newCall(request).execute()) {
      assert response.body() != null;
      final String data = response.body().string();
      final SoulSoup soulSoup = objectMapper.readValue(data, SoulSoup.class);

      log.info("fetched and indexed: {}", data);
      updateIndex(soulSoup);
    } catch (Exception e) {
      log.error("soul-soup fetch error: {}", e.getMessage(), e);
    }
  }

  private void updateIndex(SoulSoup soulSoup) throws IOException {
    final IndexRequest indexRequest = new IndexRequest(SOUL_SOUP_INDEX_NAME);
    indexRequest.id(String.valueOf(soulSoup.getId()));
    // 强制刷新数据
    indexRequest.setRefreshPolicy(RefreshPolicy.IMMEDIATE);
    indexRequest.source(objectMapper.writeValueAsString(soulSoup), XContentType.JSON);
    final IndexResponse response = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
    Assert.isTrue(
        Objects.equals(response.status(), RestStatus.CREATED),
        "文档更新失败：" + objectMapper.writeValueAsString(response));
  }

  @PreDestroy
  @Override
  public void close() {
    scheduledExecutorService.shutdown();
  }

  @Setter
  @Getter
  @AllArgsConstructor
  public static class SoulSoup {

    private Long id;
    private String title;
  }
}
