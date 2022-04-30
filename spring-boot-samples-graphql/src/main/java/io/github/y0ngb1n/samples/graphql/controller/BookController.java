package io.github.y0ngb1n.samples.graphql.controller;

import graphql.ExecutionResult;
import io.github.y0ngb1n.samples.graphql.service.GraphQLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Book Resource.
 *
 * @author yangbin
 */
@RestController
@RequestMapping(path = "/v1/books")
public class BookController {

  @Autowired private GraphQLService graphQLService;

  @PostMapping
  public ResponseEntity<Object> getAllBooks(@RequestBody String query) {
    ExecutionResult execute = graphQLService.getGraphQL().execute(query);
    return new ResponseEntity<>(execute, HttpStatus.OK);
  }
}
