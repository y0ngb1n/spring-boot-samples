package io.github.y0ngb1n.samples.urlshortener.controller;

import com.google.common.hash.Hashing;
import io.github.y0ngb1n.samples.urlshortener.exception.InvalidUrlException;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * URL Shortener Resource
 *
 * @author yangbin
 */
@Slf4j
@RestController
@RequestMapping(path = "/v1")
public class UrlShortenerController {

  @Autowired StringRedisTemplate redisTemplate;

  @GetMapping(path = "/{id}")
  public String getUrl(@PathVariable String id) {
    String url = redisTemplate.opsForValue().get(id);
    log.debug("URL Retrieved: {}", url);
    return url;
  }

  @PostMapping
  public String create(@RequestBody String url) {
    UrlValidator urlValidator = new UrlValidator(new String[] {"http", "https"});
    if (urlValidator.isValid(url)) {
      String id = Hashing.murmur3_32().hashString(url, StandardCharsets.UTF_8).toString();
      log.debug("URL Id generated: {}", id);
      redisTemplate.opsForValue().set(id, url);
      return id;
    }
    throw new InvalidUrlException("URL Invalid: " + url);
  }
}
