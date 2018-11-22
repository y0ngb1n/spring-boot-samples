package com.y0ngb1n.boot.kotlin

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

/**
 * Demo for Kotlin.
 *
 * @author y0ngb1n
 * @version 1.0.0
 */
@SpringBootApplication
class KotlinApplication

fun main(args: Array<String>) {
  SpringApplication.run(KotlinApplication::class.java, *args)
}