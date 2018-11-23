package com.y0ngb1n.boot.lombok.example;

import lombok.Getter;

/**
 * 延迟加载，可用于类和成员变量
 * <p>对于某些操作可以不必在初始化的时候加载而是等到了需要再去加载，此时就需要进行延迟加载，
 * <p>对于字段，需要添加 final 修饰.
 *
 * @see lombok.Getter
 */
public class _14_GetterLazyExample {

  @Getter(lazy = true)
  private final double[] cached = expensive();

  private double[] expensive() {
    double[] result = new double[1000000];
    for (int i = 0; i < result.length; i++) {
      result[i] = Math.asin(i);
    }
    return result;
  }
}

//  翻译成 Java 程序是：
//public class GetterLazyExample {
//  private final java.util.concurrent.AtomicReference<java.lang.Object> cached = new java.util.concurrent.AtomicReference<java.lang.Object>();
//
//  public double[] getCached() {
//    java.lang.Object value = this.cached.get();
//    if (value == null) {
//      synchronized(this.cached) {
//        value = this.cached.get();
//        if (value == null) {
//          final double[] actualValue = expensive();
//          value = actualValue == null ? this.cached : actualValue;
//          this.cached.set(value);
//        }
//      }
//    }
//    return (double[])(value == this.cached ? null : value);
//  }
//
//  private double[] expensive() {
//    double[] result = new double[1000000];
//    for (int i = 0; i < result.length; i++) {
//      result[i] = Math.asin(i);
//    }
//    return result;
//  }
//}