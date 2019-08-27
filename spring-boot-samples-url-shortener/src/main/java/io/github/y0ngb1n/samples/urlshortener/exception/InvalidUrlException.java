package io.github.y0ngb1n.samples.urlshortener.exception;

/**
 * Invalid Url Exception.
 *
 * @author yangbin
 */
public class InvalidUrlException extends RuntimeException {

  public InvalidUrlException(String message) {
    super(message);
  }
}
