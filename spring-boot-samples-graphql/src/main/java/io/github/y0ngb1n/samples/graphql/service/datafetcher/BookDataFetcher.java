package io.github.y0ngb1n.samples.graphql.service.datafetcher;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import io.github.y0ngb1n.samples.graphql.model.Book;
import io.github.y0ngb1n.samples.graphql.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Book DataFetcher.
 *
 * @author yangbin
 */
@Component
public class BookDataFetcher implements DataFetcher<Book> {

  @Autowired
  private BookRepository bookRepository;

  @Override
  public Book get(DataFetchingEnvironment dataFetchingEnvironment) {
    String isbn = dataFetchingEnvironment.getArgument("id");
    return bookRepository.findById(isbn).orElse(null);
  }
}
