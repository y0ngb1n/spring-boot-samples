package com.y0ngb1n.boot.lombok.example;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * 再也不用写 public int getFoo() {return foo;}
 *
 * 自动生成 getter/setter 方法，默认生成的方法是 public 的，如果要修改方法修饰符可以设置 AccessLevel 的值.
 *
 * @see lombok.Getter
 * @see lombok.Setter
 */
public class _05_GetterSetterExample {

  @Getter
  @Setter
  private int age = 10;

  @Setter(AccessLevel.PROTECTED)
  private String name;

  @Override
  public String toString() {
    return String.format("%s (age: %d)", name, age);
  }
}

//  翻译成 Java 程序是：
//public class _05_GetterSetterExample {
//
//  private int age = 10;
//
//  private String name;
//
//  @Override public String toString() {
//    return String.format("%s (age: %d)", name, age);
//  }
//
//  public int getAge() {
//    return age;
//  }
//
//  public void setAge(int age) {
//    this.age = age;
//  }
//
//  protected void setName(String name) {
//    this.name = name;
//  }
//}