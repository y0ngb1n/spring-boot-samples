package com.y0ngb1n.boot.lombok.example;

import lombok.Synchronized;

/**
 * 给方法加上同步锁，可用在 类方法 或者 实例方法 上，效果和 synchronized 关键字相同，区别在于锁对象不同，
 * <p>对于类方法和实例方法，synchronized 关键字的锁对象分别是类的 class 对象和 this 对象，
 * <p>而 @Synchronized 的锁对象分别是 私有静态 final 对象 lock 和 私有 final 对象 lock，
 * <p>当然，也可以自己指定锁对象。
 *
 * 现在 JDK 也比较推荐的是 Lock 对象，这个可能用的不是特别多。
 */
public class _13_SynchronizedExample {

  private final Object readLock = new Object();

  @Synchronized
  public static void hello() {
    System.out.println("world");
  }

  @Synchronized
  public int answerToLife() {
    return 42;
  }

  @Synchronized("readLock")
  public void foo() {
    System.out.println("bar");
  }
}

//  翻译成 Java 程序是：
//public class SynchronizedExample {
//  private static final Object $LOCK = new Object[0];
//  private final Object $lock = new Object[0];
//  private final Object readLock = new Object();
//
//  public static void hello() {
//    synchronized($LOCK) {
//      System.out.println("world");
//    }
//  }
//
//  public int answerToLife() {
//    synchronized($lock) {
//      return 42;
//    }
//  }
//
//  public void foo() {
//    synchronized(readLock) {
//      System.out.println("bar");
//    }
//  }
//}