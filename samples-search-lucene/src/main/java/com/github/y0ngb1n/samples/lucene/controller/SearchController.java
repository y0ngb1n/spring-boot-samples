package com.github.y0ngb1n.samples.lucene.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.search.TermQuery;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yangbin
 */
@Slf4j
@RestController
@RequestMapping(path = "/search")
@RequiredArgsConstructor
public class SearchController {

  private final Analyzer analyzer;
  private final SearcherManager searcherManager;

  @GetMapping(path = "/soul-soup/all")
  public List<String> searchAll() throws IOException {
    final MatchAllDocsQuery query = new MatchAllDocsQuery();
    return search(query, 9999);
  }

  @GetMapping(path = "/soul-soup")
  public List<String> search(String key) throws IOException, ParseException {
    final QueryParser queryParser = new QueryParser("title", analyzer);
    final Query query = queryParser.parse(key);
    return search(query, 10);
  }

  @GetMapping(path = "/soul-soup/{id}")
  public List<String> searchById(@PathVariable("id") Long id) throws IOException {
    final TermQuery query = new TermQuery(new Term("id", String.valueOf(id)));
    return search(query, 10);
  }

  private List<String> search(Query query, int numHits) throws IOException {
    log.info("Query parse => {}", query);

    searcherManager.maybeRefresh();
    final IndexSearcher searcher = searcherManager.acquire();

    final ScoreDoc[] hits = searcher.search(query, numHits).scoreDocs;
    List<String> documentList = new ArrayList<>();
    for (ScoreDoc hit : hits) {
      final Document doc = searcher.doc(hit.doc);
      documentList
        .add(String.format("score=%f, shardIndex=%d, doc=%s", hit.score, hit.shardIndex, doc));
    }
    return documentList;
  }

  @GetMapping(path = "/analyzer")
  public List<String> analyzer(String text) throws IOException {
    // 使用分析器对象的 tokenStream 方法获得一个 TokenStream 对象
    TokenStream tokenStream = analyzer.tokenStream("content", text);

    // 向 TokenStream 对象中设置一个引用，相当于一个指针
    CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);

    // 调用 TokenStream 对象的 reset 方法。如果不调用会抛异常
    tokenStream.reset();

    // 使用 while 循环遍历 TokenStream 对象
    List<String> tokens = new ArrayList<>();
    while (tokenStream.incrementToken()) {
      tokens.add(charTermAttribute.toString());
    }

    // 关闭 TokenStream 对象
    tokenStream.close();
    return tokens;
  }
}
