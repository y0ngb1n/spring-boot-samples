# 快速集成 Rest High Level Client 操作 Elasticsearch

## 依赖环境

- JDK 1.8+
- Maven 3.3.0+
- Elasticsearch 7.13.4+
- Kibana 7.13.4+ (可选)

<details>
<summary>使用 Docker 快速搭建项目环境</summary>

```yaml
version: '3.3'
services:
  elasticsearch:
    image: 'bitnami/elasticsearch:7.13.4'
    container_name: elasticsearch
    ports:
      - '9200:9200'
      - '9300:9300'
    volumes:
      - 'elasticsearch_data:/bitnami/elasticsearch/data'
  kibana:
    image: 'bitnami/kibana:7.13.4'
    container_name: kibana
    ports:
      - '5601:5601'
    volumes:
      - 'kibana_data:/bitnami/kibana'
    depends_on:
      - elasticsearch
volumes:
  elasticsearch_data:
    driver: local
  kibana_data:
    driver: local
```

</details>

## 参数配置

### Elasticsearch 分片

Elasticsearch支持 PB 级全文搜索，当索引上的数据量太大的时候，Elasticsearch 通过水平拆分的方式将一个索引上的数据拆分出来分配到不同的数据块上，拆分出来的数据库块称之为一个分片。这样做的目的实际上是让数据访问的时候，通过不同的分片规则去访问不同的数据分片，从而提供 Elasticsearch 搜索的吞吐量。

这类似于 MySQL 的分库分表，只不过 MySQL 分库分表需要借助第三方组件而 Elasticsearch 内部自身实现了此功能。

在一个多分片的索引中写入数据时，通过路由来确定具体写入哪一个分片中，所以在创建索引的时候需要指定分片的数量，并且分片的数量一旦确定就不能修改。如下代码段所示，分片和副本的数量在创建索引时通过指定的 Settings 来配置，Elasticsearch 默认为一个索引创建 5 个主分片, 并分别为每个分片创建一个副本。

### Elasticsearch 副本

副本就是分片拷贝，一个主分片有一个或多个副本分片，当主分片异常时，副本可以提供数据的查询等操作。主分片和对应的副本分片在es集群部署时是不会在同一个节点上的，所以副本分片数的最大值是 `N-1`（其中 N 为节点数）。

创建索引、创建文档、更新文档、删除文档这些请求都是写操作，必须在主分片上面完成之后才能被复制到相关的副本分片上去。ES 为了提高写入的能力这个过程是并发写的，也就是说更新文档和删除文档不互斥，ES 底层为了解决并发写的数据冲突问题，通过乐观锁的方式控制，每个文档都有一个 `_version` 版本号，当文档被修改时版本号递增。

一旦所有的副本分片都报告写成功才会向协调节点报告写操作成功，协调节点向客户端报告成功。


## 核心组件

```xml
<dependency>
  <groupId>org.elasticsearch.client</groupId>
  <artifactId>elasticsearch-rest-high-level-client</artifactId>
</dependency>
```

## 通过 Kibana 或浏览器操作

```
GET /_cat/nodes?v
GET /_cat/indices?v
GET /_search?index=soul_soup

GET /soul_soup
GET /soul_soup/_search
GET /soul_soup/_doc/666
```

## TODO

- 索引操作
  - [x] 创建
  - [x] 查询
  - [x] 删除
- 文档操作
  - [x] 创建
  - [x] 查询
  - [x] 更新
  - [x] 删除
  - [ ] 批量创建
  - [ ] 批量删除
- 文档的高级查询
  - [ ] 全量查询
  - [ ] 字段查询
  - [ ] 条件查询
  - [ ] 分页查询
  - [ ] 查询排序
  - [ ] 组合查询
  - [ ] 范围查询
  - [ ] 模糊查询
  - [ ] 高亮查询
  - [ ] 最大值查询
  - [ ] 分组查询
  
参考单元测试 [ElasticsearchClientTests](./src/test/java/io/github/y0ngb1n/samples/elasticsearch/ElasticsearchClientTests.java)

## 替代方案

### Spring Data Elasticsearch

- [官方文档](https://docs.spring.io/spring-data/elasticsearch/docs/current/reference/html/)
- [版本明细](https://docs.spring.io/spring-data/elasticsearch/docs/current/reference/html/#preface.versions)

#### 相关依赖

```xml
<dependency>
  <groupId>org.springframework.data</groupId>
  <artifactId>spring-data-elasticsearch</artifactId>
</dependency>
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-data-elasticsearch</artifactId>
</dependency>
```

## 参考链接

- [全文搜索引擎 Elasticsearch 入门教程](https://www.ruanyifeng.com/blog/2017/08/elasticsearch.html), by 阮一峰
