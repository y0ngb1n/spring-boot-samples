package com.y0ngb1n.boot.lombok.example;

import java.nio.charset.StandardCharsets;
import lombok.SneakyThrows;

/**
 * to RuntimeException 小助手，很好的隐藏了异常，有时候的确会有这样的烦恼，从某种程度上也是遵循的了 let is crash
 * <p>可以将方法中的代码用 try-catch 语句包裹起来，捕获异常并在 catch 中用 Lombok.sneakyThrow(e) 把异常抛出，
 * <p>可以使用 @SneakyThrows(Exception.class) 的形式指定抛出哪种异常。
 *
 * @see lombok.SneakyThrows;
 */
public class _12_SneakyThrowsExample implements Runnable {

  @SneakyThrows(RuntimeException.class)
  public String utf8ToString(byte[] bytes) {
    return new String(bytes, StandardCharsets.UTF_8);
  }

  @SneakyThrows
  public void run() {
    throw new Throwable();
  }
}

//  翻译成 Java 程序是：
//public class SneakyThrowsExample implements Runnable {
//  public String utf8ToString(byte[] bytes) {
//    try {
//      return new String(bytes, StandardCharsets.UTF_8);
//    } catch (RuntimeException e) {
//      throw Lombok.sneakyThrow(e);
//    }
//  }
//
//  public void run() {
//    try {
//      throw new Throwable();
//    } catch (Throwable t) {
//      throw Lombok.sneakyThrow(t);
//    }
//  }
//}
