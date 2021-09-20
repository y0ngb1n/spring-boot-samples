package com.github.y0ngb1n.samples.lucene.fetcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.springframework.stereotype.Component;

/**
 * 随机获取一句毒鸡汤 https://github.com/roojay520/cf-worker-soul-soup
 *
 * @author yangbin
 */
@Slf4j
@Component
public class SoulSoupDataFetcher implements AutoCloseable {

  private final IndexWriter indexWriter;
  private final ObjectMapper objectMapper;
  private final ScheduledExecutorService scheduledExecutorService = Executors
    .newSingleThreadScheduledExecutor();

  public SoulSoupDataFetcher(ObjectMapper objectMapper, IndexWriter indexWriter) {
    this.objectMapper = objectMapper;
    this.indexWriter = indexWriter;
    scheduledExecutorService.scheduleAtFixedRate(this::fetchSoulSoup, 0, 30, TimeUnit.SECONDS);
  }

  @PostConstruct
  public void initialize() throws IOException {
    final SoulSoup soulSoup = new SoulSoup();
    soulSoup.setId(666L);
    soulSoup.setTitle(
      "面向生产环境的多语种自然语言处理工具包，基于PyTorch和TensorFlow 2.x双引擎，目标是普及落地最前沿的NLP技术。HanLP具备功能完善、性能高效、架构清晰、语料时新、可自定义的特点。");
    updateIndex(soulSoup);
    log.info("Lucene index initialized.");
  }

  private void fetchSoulSoup() {
    OkHttpClient client = new OkHttpClient().newBuilder()
      .build();
    Request request = new Request.Builder()
      .url("https://soul-soup.fe.workers.dev/")
      .method("GET", null)
      .build();
    try (Response response = client.newCall(request).execute()) {
      assert response.body() != null;
      final String data = response.body().string();
      final SoulSoup soulSoup = objectMapper.readValue(data, SoulSoup.class);

      updateIndex(soulSoup);
      log.info("fetched and indexed: {}", data);
    } catch (Exception e) {
      log.error("soul-soup fetch error: {}", e.getMessage(), e);
    }
  }

  private void updateIndex(SoulSoup soulSoup) throws IOException {
    // 更新 Lucene 索引
    final Document doc = new Document();
    doc.add(new TextField("id", String.valueOf(soulSoup.getId()), Store.YES));
    doc.add(new TextField("title", soulSoup.getTitle(), Store.YES));
    // 先查，后删，再保存
    indexWriter.updateDocument(new Term("id", String.valueOf(soulSoup.getId())), doc);
    indexWriter.commit();
  }

  @PreDestroy
  @Override
  public void close() {
    scheduledExecutorService.shutdown();
  }

  @Setter
  @Getter
  public static class SoulSoup {

    private Long id;
    private String title;
  }
}
