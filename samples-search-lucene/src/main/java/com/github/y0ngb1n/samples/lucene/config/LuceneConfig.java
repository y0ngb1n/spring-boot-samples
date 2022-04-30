package com.github.y0ngb1n.samples.lucene.config;

import com.hankcs.hanlp.HanLP;
import com.hankcs.lucene.HanLPTokenizer;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.ControlledRealTimeReopenThread;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.SearcherFactory;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** @author yangbin */
@Slf4j
@Configuration
public class LuceneConfig implements AutoCloseable {

  /** 指定索引的存储目录 */
  private static final String INDEX_PATH = "./lucene_index";

  private IndexWriter indexWriter;
  private ControlledRealTimeReopenThread<IndexSearcher> indexSearcherReopenThread;

  /** 定期合并 Lucene 索引文件 */
  private final ScheduledExecutorService scheduledForceMergeIndexExecutor =
      Executors.newSingleThreadScheduledExecutor(
          new BasicThreadFactory.Builder().namingPattern("force-merge-index-executor").build());

  /** 中文分词分析器 */
  @Bean
  public Analyzer analyzer() {
    return new Analyzer() {
      @Override
      protected TokenStreamComponents createComponents(String fieldName) {
        // 北京大学 -> 北京, 大学, 北京大学
        final HanLPTokenizer tokenizer =
            new HanLPTokenizer(
                HanLP.newSegment()
                    // 是否启用偏移量计算（开启后Term.offset才会被计算）
                    .enableOffset(true)
                    // 是否启用所有的命名实体识别
                    // .enableAllNamedEntityRecognize(true)
                    // 索引模式下的最小切分颗粒度（设为1可以最小切分为单字）
                    .enableIndexMode(true),
                null,
                false);
        return new TokenStreamComponents(tokenizer);
      }
    };
  }

  /** 索引的存储目录 */
  @Bean
  public Directory directory() throws IOException {
    return FSDirectory.open(Paths.get(INDEX_PATH));
  }

  @Bean
  public IndexWriter indexWriter(Analyzer analyzer, Directory directory) throws IOException {
    // 构造配置对象
    final IndexWriterConfig config = new IndexWriterConfig(analyzer);
    // 构造 IndexWriter 对象
    indexWriter = new IndexWriter(directory, config);
    scheduledForceMergeIndexExecutor.scheduleAtFixedRate(
        () -> {
          try {
            log.info("IndexWriter try forceMerge.");
            indexWriter.forceMerge(3);
          } catch (IOException e) {
            log.error("IndexWriter forceMerge error: {}", e.getMessage(), e);
          }
        },
        0,
        5,
        TimeUnit.MINUTES);
    return indexWriter;
  }

  @Bean
  public SearcherManager searcherManager(IndexWriter indexWriter) throws IOException {
    final SearcherManager searcherManager =
        new SearcherManager(indexWriter, false, false, new SearcherFactory());
    // 创建守护线程，定期更新 SearchManager 管理的 IndexSearcher 加载最新的索引库
    indexSearcherReopenThread =
        new ControlledRealTimeReopenThread<>(indexWriter, searcherManager, 5.0, 0.025);
    indexSearcherReopenThread.setName("update-index-reader-thread");
    indexSearcherReopenThread.setDaemon(true);
    indexSearcherReopenThread.start();
    return searcherManager;
  }

  @PreDestroy
  @Override
  public void close() {
    try {
      indexSearcherReopenThread.interrupt();
      indexSearcherReopenThread.close();
      indexWriter.commit();
      indexWriter.close();
    } catch (IOException e) {
      log.error("Error while closing lucene index: {}", e.getMessage(), e);
    }
  }
}
