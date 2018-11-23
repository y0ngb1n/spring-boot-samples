package com.y0ngb1n.boot.lombok.example;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 把常用的注解聚合成一个，相当于 {@code @Getter @Setter @RequiredArgsConstructor @ToString @EqualsAndHashCode}.
 *
 * @see Getter                  @Getter(作用于所有字段)
 * @see Setter                  @Setter(作用于所有非 final 字段)
 * @see RequiredArgsConstructor @RequiredArgsConstructor(会对 final 和 @NonNull 字段生成对应参数的构造函数)
 * @see ToString
 * @see EqualsAndHashCode
 */
@Data
public class _09_DataExample {

  private final String name;

  @Setter(AccessLevel.PACKAGE)
  private int age;

  private double score;

  private String[] tags;

  @ToString(includeFieldNames = true)
  @Data(staticConstructor = "of")
  public static class Exercise<T> {

    private final String name;
    private final T value;
  }
}
