package com.y0ngb1n.boot.lombok.example;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * @see RequiredArgsConstructor 会对 final 和 @NonNull 字段生成对应参数的构造函数（可能带参数也可能不带参数），
 * <p>如果带参数，这参数只能是以 final 修饰的未经初始化的字段，或者是以 @NonNull 注解的未经初始化的字段，
 * <p>如果无 final 和 @NonNull 字段，会产生无参构造方法，此时如果同时使用了 {@code @NoArgsConstructor}，编译器会报错。
 * <p>通过 {@code @RequiredArgsConstructor(staticName = "of")} 会生成一个 of() 的静态方法，并把构造方法设置为私有的。
 * <p>
 * @see AllArgsConstructor 生成全参数的构造方法，会配合 @NonNull，不提供默认构造方法。
 * <p>
 * @see NoArgsConstructor 生成一个无参的构造方法，当类中有 final 字段没有被初始化时，编译器会报错，
 * <p>此时可用 {@code @NoArgsConstructor(force = true)}，然后就会为没有初始化的 final 字段设置默认值 0 / false / null。
 * <p>对于具有约束的字段（例如 @NonNull 字段），不会生成检查或分配，因此请注意，正确初始化这些字段之前，这些约束无效。
 */
@RequiredArgsConstructor(staticName = "of")
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class _08_ConstructorExample<T> {

  private int x, y;

  @NonNull
  private T description;

  @NoArgsConstructor
  public static class NoArgsExample {

    @NonNull
    private String field;
  }
}

//  翻译成 Java 程序是：
//public class _08_ConstructorExample<T> {
//  private int x, y;
//  @NonNull private T description;
//
//  private _08_ConstructorExample(T description) {
//    if (description == null) throw new NullPointerException("description");
//    this.description = description;
//  }
//
//  public static <T> _08_ConstructorExample<T> of(T description) {
//    return new _08_ConstructorExample<T>(description);
//  }
//
//  @java.beans.ConstructorProperties({"x", "y", "description"})
//  protected _08_ConstructorExample(int x, int y, T description) {
//    if (description == null) throw new NullPointerException("description");
//    this.x = x;
//    this.y = y;
//    this.description = description;
//  }
//
//  public static class NoArgsExample {
//    @NonNull private String field;
//
//    public NoArgsExample() {
//    }
//  }
//}