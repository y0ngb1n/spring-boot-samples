package com.y0ngb1n.boot.lombok.example;

import lombok.ToString;

/**
 * Debug Log 的最强帮手，会生成 toString()方法，默认情况下，它会按顺序（以逗号分隔）打印你的类名称以及每个字段.
 * <p>为字段添加 @ToString.Exclude 注解表示不包含该字段，
 * <p>如果继承父类，可以设置 callSuper 让其调用父类的 toString()方法，默认不调用，例如：@ToString(callSuper = true).
 *
 * 注：其实和 org.apache.commons.lang3.builder.ReflectionToStringBuilder 很像.
 *
 * @see lombok.ToString
 */
@ToString
public class _06_ToStringExample {

  private static final int STATIC_VAR = 10;
  private String name;
  private Shape shape = new Square(5, 10);
  private String[] tags;

  @ToString.Exclude
  private int id;

  public String getName() {
    return this.name;
  }

  @ToString(callSuper = true, includeFieldNames = true)
  public static class Square extends Shape {

    private final int width;
    private final int height;

    public Square(int width, int height) {
      this.width = width;
      this.height = height;
    }
  }
}

//  翻译成 Java 程序是：
//  public class _06_ToStringExample {
//    private static final int STATIC_VAR = 10;
//    private String name;
//    private Shape shape = new Square(5, 10);
//    private String[] tags;
//    private int id;
//
//    public String getName() {
//      return this.getName();
//    }
//
//    public static class Square {
//      private final int width;
//      private final int height;
//
//      public Square(int width, int height) {
//        this.width = width;
//        this.height = height;
//      }
//
//      @Override
//      public String toString() {
//        return "_06_ToStringExample.Square(super=" + super.toString() + ", width=" + this.width + ", height=" + this.height + ")";
//      }
//    }
//
//    @Override
//    public String toString() {
//      return "_06_ToStringExample(name=" + this.getName() + ", shape=" + this.shape + ", tags=" + Arrays.deepToString(this.tags) + ")";
//    }
//  }

