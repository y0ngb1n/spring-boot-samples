package io.github.y0ngb1n.samples.graphql;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

/**
 * 集成测试
 *
 * @author yangbin
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.MOCK, classes = GraphQLApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-integration-test.yml")
public class GraphQLApplicationIntegrationTest {

  @Autowired
  private MockMvc mvc;

  @Test
  public void givenAllBooks_whenGetAllBooks_thenStatus200() throws Exception {
    mvc.perform(post("/v1/books")
      .contentType(MediaType.TEXT_PLAIN)
      .content("{allBooks {isbn title}}"))
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
  }

  @Test
  public void givenAllBooks_whenGetAllBooks_thenReturnBookTitle() throws Exception {
    mvc.perform(post("/v1/books")
      .contentType(MediaType.TEXT_PLAIN)
      .content("{allBooks {title}}"))
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.data.allBooks", hasSize(5)))
      .andExpect(jsonPath("$.data.allBooks[0].isbn").doesNotExist())
      .andExpect(jsonPath("$.data.allBooks[0].title").isNotEmpty());
  }

  @Test
  public void givenBook_whenGetBookFindById_thenReturnBook() throws Exception {
    mvc.perform(post("/v1/books")
      .contentType(MediaType.TEXT_PLAIN)
      .content("{book(id: \"9787121362132\") {isbn title authors}}"))
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.data.book").exists())
      .andExpect(jsonPath("$.data.book.isbn").value("9787121362132"))
      .andExpect(jsonPath("$.data.book.title").value("高可用可伸缩微服务架构：基于 Dubbo、Spring Cloud 和 Service Mesh"))
      .andExpect(jsonPath("$.data.book.authors").isArray());
  }

  @Test
  public void givenAllBooksAndBook_whenAllBooks_thenReturnAllBooksAndBook() throws Exception {
    mvc.perform(post("/v1/books")
      .contentType(MediaType.TEXT_PLAIN)
      .content("{allBooks {isbn title} book(id: \"9787121362132\") {isbn title authors}}"))
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.data.allBooks").isArray())
      .andExpect(jsonPath("$.data.book").exists());
  }
}
