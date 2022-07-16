package io.github.y0ngb1n.samples.elasticsearch;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.y0ngb1n.samples.elasticsearch.fetcher.SoulSoupDataFetcher;
import io.github.y0ngb1n.samples.elasticsearch.fetcher.SoulSoupDataFetcher.SoulSoup;
import java.io.IOException;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Elasticsearch Client 单元测试
 *
 * @author yangbin
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class ElasticsearchClientTests {

  private static final String TEST_INDEX_NAME = "test-index";

  @Resource private ObjectMapper objectMapper;
  @Resource private RestHighLevelClient restHighLevelClient;

  /** 创建索引库 */
  @Before
  public void createIndex() throws IOException {
    this.deleteIndex();

    final CreateIndexRequest createIndexRequest = new CreateIndexRequest(TEST_INDEX_NAME);
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
    log.info("created index:{}, response:{}.", TEST_INDEX_NAME, response.isAcknowledged());
  }

  /** 删除索引库 */
  @After
  public void deleteIndex() throws IOException {
    final GetIndexRequest getIndexRequest = new GetIndexRequest(TEST_INDEX_NAME);
    final boolean indexExists =
        restHighLevelClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
    if (!indexExists) {
      log.warn("index:{} not found, skip delete.", TEST_INDEX_NAME);
      return;
    }

    final DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(TEST_INDEX_NAME);
    try {
      final AcknowledgedResponse response =
          restHighLevelClient.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
      log.info("deleted index:{}, response:{}.", TEST_INDEX_NAME, response.isAcknowledged());
    } catch (RuntimeException e) {
      log.error("delete index:{} error.", TEST_INDEX_NAME, e);
    }
  }

  /** 测试新增文档 */
  @Test
  public void test_create() throws IOException {
    SoulSoup insertDoc = new SoulSoup(1L, "Test insert doc.");
    this.create(insertDoc);
  }

  private void create(SoulSoup doc) throws IOException {
    final IndexRequest indexRequest = new IndexRequest(TEST_INDEX_NAME);
    indexRequest.id(String.valueOf(doc.getId()));
    // 强制刷新数据
    indexRequest.setRefreshPolicy(RefreshPolicy.IMMEDIATE);
    indexRequest.source(objectMapper.writeValueAsString(doc), XContentType.JSON);
    final IndexResponse response = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
    log.info("inserted doc, response:{}.", objectMapper.writeValueAsString(response));
    Assert.assertEquals(RestStatus.CREATED, response.status());
  }

  /** 测试根据ID查询文档 */
  @Test
  public void test_query_getById() throws IOException {
    final SoulSoup doc = SoulSoupDataFetcher.DEFAULT_SOUL_SOUP_DOC;
    create(doc);
    queryById(doc);
  }

  private void queryById(SoulSoup doc) throws IOException {
    final long docId = doc.getId();
    final SearchRequest searchRequest = new SearchRequest(TEST_INDEX_NAME);
    final SearchSourceBuilder queryBuilder =
        new SearchSourceBuilder().query(QueryBuilders.termQuery("id", docId));
    searchRequest.source(queryBuilder);

    final SearchResponse searchResponse =
        restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
    Assert.assertEquals(RestStatus.OK, searchResponse.status());

    final SearchHits searchHits = searchResponse.getHits();
    // 通过ID查询的数据有且只有1条
    Assert.assertEquals(1, searchHits.getTotalHits().value);
    log.info("search id:{}, response:{}.", docId, objectMapper.writeValueAsString(searchResponse));

    // 判断查询的数据与插入的数据是否一致
    final String dataJson = searchHits.getHits()[0].getSourceAsString();
    Assert.assertEquals(objectMapper.writeValueAsString(doc), dataJson);
  }

  /** 测试更新文档 */
  @Test
  public void test_update() throws IOException {
    SoulSoup updateDoc = new SoulSoup(2L, "New test doc.");
    // 插入数据
    this.create(updateDoc);

    // 更新数据
    updateDoc.setTitle("Update test doc.");
    final UpdateRequest updateRequest =
        new UpdateRequest(TEST_INDEX_NAME, String.valueOf(updateDoc.getId()));
    updateRequest.doc(objectMapper.writeValueAsString(updateDoc), XContentType.JSON);
    // 强制刷新
    updateRequest.setRefreshPolicy(RefreshPolicy.IMMEDIATE);
    final UpdateResponse response =
        restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
    log.info("updated doc, response:{}.", objectMapper.writeValueAsString(response));
    Assert.assertEquals(RestStatus.OK, response.status());

    // 查询更新结果
    this.queryById(updateDoc);
  }

  /** 测试删除文档 */
  @Test
  public void test_remove_deleteById() throws IOException {
    SoulSoup deleteDoc = new SoulSoup(3L, "Test delete doc.");
    this.create(deleteDoc);

    final DeleteRequest deleteRequest =
        new DeleteRequest(TEST_INDEX_NAME, String.valueOf(deleteDoc.getId()));
    // 强制刷新
    deleteRequest.setRefreshPolicy(RefreshPolicy.IMMEDIATE);
    final DeleteResponse response =
        restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
    log.info("deleted doc, response:{}.", objectMapper.writeValueAsString(response));
    Assert.assertEquals(RestStatus.OK, response.status());

    // 查询数据
    final SearchRequest searchRequest = new SearchRequest(TEST_INDEX_NAME);
    final SearchSourceBuilder queryBuilder =
        new SearchSourceBuilder().query(QueryBuilders.termQuery("id", deleteDoc.getId()));
    searchRequest.source(queryBuilder);

    final SearchResponse searchResponse =
        restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
    Assert.assertEquals(RestStatus.OK, searchResponse.status());

    final SearchHits searchHits = searchResponse.getHits();
    // 通过ID查询应无数据
    Assert.assertEquals(0, searchHits.getTotalHits().value);
  }
}
