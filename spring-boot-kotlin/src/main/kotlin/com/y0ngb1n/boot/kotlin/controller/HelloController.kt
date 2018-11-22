package com.y0ngb1n.boot.kotlin.controller

import com.y0ngb1n.boot.kotlin.domain.HelloDto
import com.y0ngb1n.boot.kotlin.service.HelloService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloController(val helloService: HelloService) {

  @GetMapping("/hello")
  fun helloKotlin(): String {
    return "hello kotlin"
  }

  @GetMapping("/hello-service")
  fun helloKotlinService(): String {
    return helloService.getHello()
  }

  @GetMapping("/hello-dto")
  fun helloDto(): HelloDto {
    return HelloDto("Hello from the DTO")
  }
}