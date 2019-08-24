package io.github.y0ngb1n.samples.graphql.repository;

import io.github.y0ngb1n.samples.graphql.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Book Repository.
 *
 * @author yangbin
 */
@Repository
public interface BookRepository extends JpaRepository<Book, String> {

}
