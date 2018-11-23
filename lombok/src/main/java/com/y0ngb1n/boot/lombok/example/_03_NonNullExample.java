package com.y0ngb1n.boot.lombok.example;

import lombok.NonNull;

/**
 * 会自动产生一个关于此参数的非空检查，如果为空会直接抛出 NullPointerException.
 *
 * @see lombok.NonNull
 */
public class _03_NonNullExample extends Mountain {

  private String name;

  public _03_NonNullExample(@NonNull User user) {
    super("Hello");
    this.name = user.getName();
  }

//  翻译成 Java 程序是：
//  public _03_NonNullExample(@NonNull User user) {
//    super("Hello");
//    if (user == null) {
//      throw new NullPointerException("user is marked @NonNull but is null");
//    }
//    this.name = user.getName();
//  }
}
