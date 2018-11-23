package com.y0ngb1n.boot.lombok.example;

import lombok.AccessLevel;
import lombok.ToString;
import lombok.Value;
import lombok.experimental.NonFinal;
import lombok.experimental.Wither;

/**
 * 我们发现了 @Value 就是 @Data 的不可变版本。至于不可变有什么好处。可有参看此篇（https://blogs.msdn.microsoft.com/ericlippert/2007/11/13/immutability-in-c-part-one-kinds-of-immutability/）
 *
 * @see lombok.Value
 */
@Value
public class _10_ValueExample {

  String name;

  @Wither(AccessLevel.PACKAGE)
  @NonFinal
  int age;

  double score;

  protected String[] tags;

  @ToString(includeFieldNames = true)
  @Value(staticConstructor = "of")
  public static class Exercise<T> {

    String name;
    T value;
  }
}
