package io.github.y0ngb1n.samples.graphql.service.datafetcher;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import io.github.y0ngb1n.samples.graphql.model.Book;
import io.github.y0ngb1n.samples.graphql.repository.BookRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * All Books DataFetcher.
 *
 * @author yangbin
 */
@Component
public class AllBooksDataFetcher implements DataFetcher<List<Book>> {

  @Autowired
  private BookRepository bookRepository;

  @Override
  public List<Book> get(DataFetchingEnvironment dataFetchingEnvironment) {
    return bookRepository.findAll();
  }
}
