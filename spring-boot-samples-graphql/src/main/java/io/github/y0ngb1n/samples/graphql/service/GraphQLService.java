package io.github.y0ngb1n.samples.graphql.service;

import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import io.github.y0ngb1n.samples.graphql.model.Book;
import io.github.y0ngb1n.samples.graphql.repository.BookRepository;
import io.github.y0ngb1n.samples.graphql.service.datafetcher.AllBooksDataFetcher;
import io.github.y0ngb1n.samples.graphql.service.datafetcher.BookDataFetcher;
import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

/**
 * GraphQL Service.
 *
 * @author yangbin
 */
@Service
public class GraphQLService {

  @Value("classpath:schema.graphql")
  private Resource resource;

  @Autowired private BookRepository bookRepository;

  @Getter private GraphQL graphQL;

  @Autowired private AllBooksDataFetcher allBooksDataFetcher;

  @Autowired private BookDataFetcher bookDataFetcher;

  @PostConstruct
  private void loadSchema() throws IOException {
    loadDataIntoHSQL();
    // 获取本地定义的 Schema 文件
    File schemaFile = resource.getFile();
    // 解析 Schema 文件
    TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(schemaFile);
    RuntimeWiring wiring = buildRuntimeWiring();
    GraphQLSchema schema = new SchemaGenerator().makeExecutableSchema(typeRegistry, wiring);
    graphQL = GraphQL.newGraphQL(schema).build();
  }

  private void loadDataIntoHSQL() {
    Stream.of(
            new Book(
                "9787111213826",
                "Java 编程思想（第4版）",
                "机械工业出版社",
                new String[] {"Bruce Eckel"},
                "2007-06-01"),
            new Book(
                "9787115221704",
                "重构 改善既有代码的设计（第2版）",
                "人民邮电出版社",
                new String[] {"Martin Fowler"},
                "2019-05-01"),
            new Book(
                "9787302392644",
                "人月神话（40周年中文纪念版）",
                "清华大学出版社",
                new String[] {"Brooks"},
                "2015-04-01"),
            new Book(
                "9787111421900",
                "深入理解 Java 虚拟机：JVM 高级特性与最佳实践（第2版）",
                "机械工业出版社",
                new String[] {"周志明"},
                "2013-05-01"),
            new Book(
                "9787121362132",
                "高可用可伸缩微服务架构：基于 Dubbo、Spring Cloud 和 Service Mesh",
                "电子工业出版社",
                new String[] {"程超", "梁桂钊", "秦金卫", "方志斌", "张逸", "杜琪", "殷琦", "肖冠宇"},
                "2019-05-01"))
        .forEach(book -> bookRepository.save(book));
  }

  private RuntimeWiring buildRuntimeWiring() {
    return RuntimeWiring.newRuntimeWiring()
        .type(
            "Query",
            typeWiring ->
                typeWiring
                    .dataFetcher("allBooks", allBooksDataFetcher)
                    .dataFetcher("book", bookDataFetcher))
        .build();
  }
}
