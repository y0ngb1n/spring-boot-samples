package com.y0ngb1n.boot.lombok.example;

import java.util.ArrayList;
import java.util.HashMap;
import lombok.val;

/**
 * 使用 val 声明常量: val x = 10; >> final int x = 10;
 *
 * @see lombok.val
 */
public class _01_ValExample {

  public String example() {
    val example = new ArrayList<String>();
    example.add("Hello, World!");
    val foo = example.get(0);
    return foo.toLowerCase();
  }

  public void example2() {
    val map = new HashMap<Integer, String>();
    map.put(0, "zero");
    map.put(5, "five");
    for (val entry : map.entrySet()) {
      System.out.printf("%d: %s\n", entry.getKey(), entry.getValue());
    }
  }

//  翻译成 Java 程序是：
//  public String example() {
//    final ArrayList<String> example = new ArrayList<String>();
//    example.add("Hello, World!");
//    final String foo = example.get(0);
//    return foo.toLowerCase();
//  }
//
//  public void example2() {
//    final HashMap<Integer, String> map = new HashMap<Integer, String>();
//    map.put(0, "zero");
//    map.put(5, "five");
//    for (final Map.Entry<Integer, String> entry : map.entrySet()) {
//      System.out.printf("%d: %s\n", entry.getKey(), entry.getValue());
//    }
//  }

  public static void main(String[] args) {
    val bar = new _01_ValExample();
    System.out.println(bar.example());

    bar.example2();
  }
}
