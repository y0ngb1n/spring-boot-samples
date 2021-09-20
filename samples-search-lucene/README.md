# Apache Lucene 搜索入门

## 核心类

- org.apache.lucene.analysis.Analyzer
- org.apache.lucene.store.Directory
- org.apache.lucene.index.IndexWriter
- org.apache.lucene.search.SearcherManager
- org.apache.lucene.document.Document

## 初体验

查询全部文档：`/search/soul-soup/all`

```json
[
  "score=1.000000, shardIndex=0, doc=Document<stored,indexed,tokenized<id:-1> stored,indexed,tokenized<title:面向生产环境的多语种自然语言处理工具包，基于PyTorch和TensorFlow 2.x双引擎，目标是普及落地最前沿的NLP技术。HanLP具备功能完善、性能高效、架构清晰、语料时新、可自定义的特点。>>",
  "score=1.000000, shardIndex=0, doc=Document<stored,indexed,tokenized<id:228> stored,indexed,tokenized<title:学习使人快乐，不学习使人，更快乐。>>",
  "score=1.000000, shardIndex=0, doc=Document<stored,indexed,tokenized<id:376> stored,indexed,tokenized<title:为什么总是天妒英才呢？因为没人管笨蛋活多久。>>"
]
```

自定义查询条件：`/search/soul-soup?key=-id:0 title:自然语言处理工具包NLP`

```console
2021-09-20 15:17:45.573  INFO 6892 --- [io-8080-exec-10] c.g.y.s.l.controller.SearchController    : Query parse: -id:0 (title:自然 title:语言 title:处理 title:工具包 title:工具 title:工 title:具 title:包 title:NLP)
```

```json
[
  "score=9.497667, shardIndex=0, doc=Document<stored,indexed,tokenized<id:666> stored,indexed,tokenized<title:面向生产环境的多语种自然语言处理工具包，基于PyTorch和TensorFlow 2.x双引擎，目标是普及落地最前沿的NLP技术。HanLP具备功能完善、性能高效、架构清晰、语料时新、可自定义的特点。>>",
  "score=2.091718, shardIndex=0, doc=Document<stored,indexed,tokenized<id:1080> stored,indexed,tokenized<title:车到山前必有雾，船到桥头自然沉。>>",
  "score=1.466976, shardIndex=0, doc=Document<stored,indexed,tokenized<id:841> stored,indexed,tokenized<title:每次看你穿丝袜的时候，我都有一种无法言喻的感觉，那就是萝卜还包保鲜膜咧。>>"
]
```

调试分词器：`/search/analyzer?text=生活就像海洋，只有意志坚强的人，才能到达彼岸。`

```json
[
  "生活",
  "就",
  "像",
  "海洋",
  "，",
  "只有",
  "意志",
  "坚强",
  "的",
  "人",
  "，",
  "才能",
  "到达",
  "彼岸",
  "。"
]
```

## 解锁更多姿势

- [Apache Lucene 入门指南](https://www.yuque.com/y0ngb1n/apache-lucene-tutorials)

## 参考链接

- https://www.yuque.com/y0ngb1n/apache-lucene-tutorials
- https://github.com/hankcs/HanLP
  - https://github.com/hankcs/HanLP/tree/1.x
  - https://github.com/hankcs/hanlp-lucene-plugin
  - https://mvnrepository.com/artifact/com.hankcs/hanlp
- https://github.com/magese/ik-analyzer-solr
