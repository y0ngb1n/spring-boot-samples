package com.y0ngb1n.boot.kotlin

import com.y0ngb1n.boot.kotlin.domain.HelloDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(
    classes = [KotlinApplication::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class KotlinApplicationTests {

  @Autowired
  lateinit var testRestTemplate: TestRestTemplate

  @Test
  fun testHelloKotlin() {
    val result = testRestTemplate
        // ...
        .getForEntity("/hello", String::class.java)

    assertNotNull(result)
    assertEquals(result?.statusCode, HttpStatus.OK)
    assertEquals(result?.body, "hello kotlin")
  }

  @Test
  fun testHelloKotlinService() {
    val result = testRestTemplate
        // ...
        .getForEntity("/hello-service", String::class.java)

    assertNotNull(result)
    assertEquals(result?.statusCode, HttpStatus.OK)
    assertEquals(result?.body, "hello service")
  }

  @Test
  fun testHelloDto() {
    val result = testRestTemplate
        // ...
        .getForEntity("/hello-dto", HelloDto::class.java)

    assertNotNull(result)
    assertEquals(result?.statusCode, HttpStatus.OK)
    assertEquals(result?.body, HelloDto("Hello from the DTO"))
  }
}