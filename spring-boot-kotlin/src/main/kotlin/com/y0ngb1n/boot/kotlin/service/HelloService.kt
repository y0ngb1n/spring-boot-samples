package com.y0ngb1n.boot.kotlin.service

import org.springframework.stereotype.Service

@Service
class HelloService {

  fun getHello(): String {
    return "hello service"
  }
}