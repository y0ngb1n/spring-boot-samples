package io.github.y0ngb1n.samples.urlshortener;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

/**
 * @author yangbin
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.MOCK, classes = UrlShortenerApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-integration-test.properties")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UrlShortenerApplicationIntegrationTest {

  @Value("${integration-test.valid-url}")
  private String testValidUrl;
  @Value("${integration-test.valid-url-id}")
  private String testValidUrlId;
  @Value("${integration-test.invalid-url}")
  private String testInvalidUrl;

  @Autowired
  private MockMvc mvc;

  @Test
  public void a_givenUrlId_whenCreateUrlId_thenStatus200() throws Exception {
    mvc.perform(post("/v1")
      .contentType(MediaType.TEXT_PLAIN)
      .content(testValidUrl))
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN));
  }

  @Test
  public void b_givenUrlId_whenCreateUrlId_thenCreateSuccessful() throws Exception {
    mvc.perform(post("/v1")
      .contentType(MediaType.TEXT_PLAIN)
      .content(testValidUrl))
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
      .andExpect(content().string(testValidUrlId));
  }

  @Test(expected = Exception.class)
  public void b_givenUrlId_whenCreateUrlId_thenCreateFailure() throws Exception {
    mvc.perform(post("/v1")
      .contentType(MediaType.TEXT_PLAIN)
      .content(testInvalidUrl));
  }

  @Test
  public void c_givenUrl_whenGetUrl_thenRetrievedSuccessful() throws Exception {
    mvc.perform(get("/v1/{id}", testValidUrlId))
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
      .andExpect(content().string(testValidUrl));
  }
}
