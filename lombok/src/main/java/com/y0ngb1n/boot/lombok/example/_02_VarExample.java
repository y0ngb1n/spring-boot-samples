package com.y0ngb1n.boot.lombok.example;

import java.util.ArrayList;
import java.util.HashMap;
import lombok.var;
/**
 * 使用 var 声明变量: val x = 10; >> int x = 10;
 *
 * @see lombok.var
 */
public class _02_VarExample {

  public String example() {
    var example = new ArrayList<String>();
    example.add("Hello, World!");
    var foo = example.get(0);
    return foo.toLowerCase();
  }

  public void example2() {
    var map = new HashMap<Integer, String>();
    map.put(0, "zero");
    map.put(5, "five");
    for (var entry : map.entrySet()) {
      System.out.printf("%d: %s\n", entry.getKey(), entry.getValue());
    }
  }

//  翻译成 Java 程序是：
//  public String example() {
//    ArrayList<String> example = new ArrayList<String>();
//    example.add("Hello, World!");
//    String foo = example.get(0);
//    return foo.toLowerCase();
//  }
//
//  public void example2() {
//    HashMap<Integer, String> map = new HashMap<Integer, String>();
//    map.put(0, "zero");
//    map.put(5, "five");
//    for (Map.Entry<Integer, String> entry : map.entrySet()) {
//      System.out.printf("%d: %s\n", entry.getKey(), entry.getValue());
//    }
//  }

  public static void main(String[] args) {
    var bar = new _02_VarExample();
    System.out.println(bar.example());

    bar.example2();
  }
}
