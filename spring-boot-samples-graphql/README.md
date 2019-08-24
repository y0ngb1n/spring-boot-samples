# GraphQL 与 Spring Boot 的初体验

> GraphQL 既是一种用于 API 的查询语言也是一个满足你数据查询的运行时。 GraphQL 对你的 API 中的数据提供了一套易于理解的完整描述，使得客户端能够准确地获得它需要的数据，而且没有任何冗余，也让 API 更容易地随着时间推移而演进，还能用于构建强大的开发者工具。

## 定义 Schema

```graphql
# src/main/resources/schema.graphql
schema {
  query: Query
}

type Query {
  allBooks: [Book]
  book(id: String): Book
}

type Book {
  isbn: String
  title: String
  publisher: String
  authors: [String]
  publishedDate: String
}
```

## 加载并解析上面定义的 Schema

```java
@Service
public class GraphQLService {

  @Value("classpath:schema.graphql")
  private Resource resource;

  @Getter
  private GraphQL graphQL;
  @Autowired
  private AllBooksDataFetcher allBooksDataFetcher;
  @Autowired
  private BookDataFetcher bookDataFetcher;

  @PostConstruct
  private void loadSchema() throws IOException {
    // 获取本地定义的 Schema 文件
    File schemaFile = resource.getFile();
    // 解析 Schema 文件
    TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(schemaFile);
    RuntimeWiring wiring = buildRuntimeWiring();
    GraphQLSchema schema = new SchemaGenerator().makeExecutableSchema(typeRegistry, wiring);
    graphQL = GraphQL.newGraphQL(schema).build();
  }

  private RuntimeWiring buildRuntimeWiring() {
    return RuntimeWiring.newRuntimeWiring()
      .type("Query", typeWiring -> typeWiring
        .dataFetcher("allBooks", allBooksDataFetcher)
        .dataFetcher("book", bookDataFetcher)
      ).build();
  }
}
```

## 提供 DataFetcher

相当于提供 Schema 中的 Query 实现：

```graphql
type Query {
  allBooks: [Book]
  book(id: String): Book
}
```

`AllBooksDataFetcher` 对应实现 `allBooks: [Book]`：

```java
@Component
public class AllBooksDataFetcher implements DataFetcher<List<Book>> {

  @Autowired
  private BookRepository bookRepository;

  @Override
  public List<Book> get(DataFetchingEnvironment dataFetchingEnvironment) {
    return bookRepository.findAll();
  }
}
```

`BookDataFetcher` 对应实现 `book(id: String): Book`：

```java
@Component
public class BookDataFetcher implements DataFetcher<Book> {

  @Autowired
  private BookRepository bookRepository;

  @Override
  public Book get(DataFetchingEnvironment dataFetchingEnvironment) {
    String isn = dataFetchingEnvironment.getArgument("id");
    return bookRepository.findById(isn).orElse(null);
  }
}
```

## 提供 GraphQL API

```java
@RestController
@RequestMapping(path = "/v1/books")
public class BookController {

  @Autowired
  private GraphQLService graphQLService;

  @PostMapping
  public ResponseEntity<Object> getAllBooks(@RequestBody String query) {
    ExecutionResult execute = graphQLService.getGraphQL().execute(query);
    return new ResponseEntity<>(execute, HttpStatus.OK);
  }
}
```

## 启动并测试

```console
$ mvn install
...
[INFO] BUILD SUCCESS
...
$ mvn spring-boot:run
...
2019-08-24 19:35:11.700  INFO 14464 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
2019-08-24 19:35:11.702  INFO 14464 --- [           main] i.g.y.s.graphql.GraphQLApplication       : Started GraphQLApplication in 16.808 seconds (JVM running for 25.601)
```

```console
$ curl -X POST \
  http://127.0.0.1:8080/v1/books \
  -H 'Content-Type: text/plain' \
  -d '{
    allBooks {
      isbn
      title
  }
}' | jq
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100   579    0   524  100    55  34933   3666 --:--:-- --:--:-- --:--:-- 38600
{
  "errors": [],
  "data": {
    "allBooks": [
      {
        "isbn": "9787111213826",
        "title": "Java 编程思想（第4版）"
      },
      ...
    ]
  },
  "extensions": null,
  "dataPresent": true
}
```

```console
$ curl -X POST \
  http://127.0.0.1:8080/v1/books \
  -H 'Content-Type: text/plain' \
  -d '{
    allBooks {
      isbn
      title
      authors
      publisher
      publishedDate
  }
}' | jq
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100  1139    0  1044  100    95    750     68  0:00:01  0:00:01 --:--:--   818
{
  "errors": [],
  "data": {
    "allBooks": [
      {
        "isbn": "9787111213826",
        "title": "Java 编程思想（第4版）",
        "authors": [
          "Bruce Eckel"
        ],
        "publisher": "机械工业出版社",
        "publishedDate": "2007-06-01"
      },
      {
        "isbn": "9787111421900",
        "title": "深入理解 Java 虚拟机：JVM 高级特性与最佳实践（第2版）",
        "authors": [
          "周志明"
        ],
        "publisher": "机械工业出版社",
        "publishedDate": "2013-05-01"
      },
      ...
    ]
  },
  "extensions": null,
  "dataPresent": true
}
```

```console
$ curl -X POST \
  http://127.0.0.1:8080/v1/books \
  -H 'Content-Type: text/plain' \
  -d '{
    book(id: "9787121362132") {
      title
  }
}' | jq
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100   210    0   159  100    51   1691    542 --:--:-- --:--:-- --:--:--  2234
{
  "errors": [],
  "data": {
    "book": {
      "title": "高可用可伸缩微服务架构：基于 Dubbo、Spring Cloud 和 Service Mesh"
    }
  },
  "extensions": null,
  "dataPresent": true
}
```

```console
$ curl -X POST \
  http://127.0.0.1:8080/v1/books \
  -H 'Content-Type: text/plain' \
  -d '{
    book(id: "9787121362132") {
      title
      authors
      publisher
      publishedDate
  }
}' | jq
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100   421    0   320  100   101   312k    98k --:--:-- --:--:-- --:--:--  411k
{
  "errors": [],
  "data": {
    "book": {
      "title": "高可用可伸缩微服务架构：基于 Dubbo、Spring Cloud 和 Service Mesh",
      "authors": [
        "程超",
        "梁桂钊",
        "秦金卫",
        "方志斌",
        "张逸",
        "杜琪",
        "殷琦",
        "肖冠宇"
      ],
      "publisher": "电子工业出版社",
      "publishedDate": "2019-05-01"
    }
  },
  "extensions": null,
  "dataPresent": true
}
```

```console
$ curl -X POST \
  http://127.0.0.1:8080/v1/books \
  -H 'Content-Type: text/plain' \
  -d '{
    allBooks {
      isbn
      title
    }
    book(id: "9787121362132") {
      title
      authors
      publisher
      publishedDate
  }
}' | jq
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100   930    0   785  100   145   3866    714 --:--:-- --:--:-- --:--:--  4581
{
  "errors": [],
  "data": {
    "allBooks": [
      {
        "isbn": "9787111213826",
        "title": "Java 编程思想（第4版）"
      },
      {
        "isbn": "9787111421900",
        "title": "深入理解 Java 虚拟机：JVM 高级特性与最佳实践（第2版）"
      },
      {
        "isbn": "9787115221704",
        "title": "重构 改善既有代码的设计（第2版）"
      },
      {
        "isbn": "9787121362132",
        "title": "高可用可伸缩微服务架构：基于 Dubbo、Spring Cloud 和 Service Mesh"
      },
      {
        "isbn": "9787302392644",
        "title": "人月神话（40周年中文纪念版）"
      }
    ],
    "book": {
      "title": "高可用可伸缩微服务架构：基于 Dubbo、Spring Cloud 和 Service Mesh",
      "authors": [
        "程超",
        "梁桂钊",
        "秦金卫",
        "方志斌",
        "张逸",
        "杜琪",
        "殷琦",
        "肖冠宇"
      ],
      "publisher": "电子工业出版社",
      "publishedDate": "2019-05-01"
    }
  },
  "extensions": null,
  "dataPresent": true
}
```

综上可见，API 不变只改动了查询的内容，就会自动响应不同的结果。

---

## 参考链接

+ https://graphql.org/
+ https://developer.github.com/v4/
+ https://www.baeldung.com/graphql
+ https://www.baeldung.com/spring-graphql
+ https://youtu.be/zX2I7-aIldE
+ https://leader.js.cool/#/basic/db/graphql
+ https://github.com/glennreyes/graphpack
+ [全面解析 GraphQL，携程微服务背景下的前后端数据交互方案](https://www.infoq.cn/article/xZ0ws6_A5jmrJ6ZTPOz8)
+ [前端er了解 GraphQL，看这篇就够了](https://juejin.im/post/5ca1b7be51882543ea4b7f27)
+ [GraphQL 之路](https://www.robinwieruch.de/the-road-to-graphql-book/)
