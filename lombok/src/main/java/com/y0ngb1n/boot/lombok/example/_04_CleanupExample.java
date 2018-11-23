package com.y0ngb1n.boot.lombok.example;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import lombok.Cleanup;

/**
 * 只可用于可释放的资源，确切的说是实现了 Closeable 的类，声明了此注解的变量会在结束时自动释放资源,
 * <p>默认是调用资源的 close() 方法，如果该资源有其它关闭方法，可使用 @Cleanup(“methodName”) 来指定要调用的方法.
 *
 * 注：从 JKD7 开始就已经提供 try with resource
 *
 * @see lombok.Cleanup
 */
public class _04_CleanupExample {

  public static void main(String[] args) throws IOException {
    @Cleanup InputStream in = new FileInputStream(args[0]);
    @Cleanup OutputStream out = new FileOutputStream(args[1]);
    byte[] b = new byte[10000];
    while (true) {
      int r = in.read(b);
      if (r == -1) {
        break;
      }
      out.write(b, 0, r);
    }
  }

//  翻译成 Java 程序是：
//  public static void main(String[] args) throws IOException {
//    InputStream in = new FileInputStream(args[0]);
//    try {
//      OutputStream out = new FileOutputStream(args[1]);
//      try {
//        byte[] b = new byte[10000];
//        while (true) {
//          int r = in.read(b);
//          if (r == -1) break;
//          out.write(b, 0, r);
//        }
//      } finally {
//        if (out != null) {
//          out.close();
//        }
//      }
//    } finally {
//      if (in != null) {
//        in.close();
//      }
//    }
//  }
}
