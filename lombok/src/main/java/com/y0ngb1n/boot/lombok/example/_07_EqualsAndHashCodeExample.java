package com.y0ngb1n.boot.lombok.example;

import lombok.EqualsAndHashCode;

/**
 * 生成 equals()、hashCode()、canEqual() 方法，默认情况下，它将使用所有非静态，非 transient 字段。
 * <p>但可以通过 {@code @EqualsAndHashCode.Exclude} 来排除更多字段。
 * <p>或者，通过 {@code @EqualsAndHashCode.Include} 来准确指定希望使用哪些字段。
 *
 * @see lombok.EqualsAndHashCode
 */
@EqualsAndHashCode
public class _07_EqualsAndHashCodeExample {

  private transient int transientVar = 10;

  @EqualsAndHashCode.Include
  private String name;

  private double score;

  @EqualsAndHashCode.Exclude
  private Shape shape = new Square(5, 10);

  private String[] tags;

  @EqualsAndHashCode.Exclude
  private int id;

  public String getName() {
    return this.name;
  }

  @EqualsAndHashCode(callSuper = true)
  public static class Square extends Shape {

    private final int width, height;

    public Square(int width, int height) {
      this.width = width;
      this.height = height;
    }
  }
}

//  翻译成 Java 程序是：
//public class _07_EqualsAndHashCodeExample {
//  private transient int transientVar = 10;
//  private String name;
//  private double score;
//  private Shape shape = new Square(5, 10);
//  private String[] tags;
//  private int id;
//
//  public String getName() {
//    return this.name;
//  }
//
//  @Override
//  public boolean equals(Object o) {
//    if (o == this) return true;
//    if (!(o instanceof _07_EqualsAndHashCodeExample)) return false;
//    _07_EqualsAndHashCodeExample other = (_07_EqualsAndHashCodeExample) o;
//    if (!other.canEqual((Object)this)) return false;
//    if (this.getName() == null ? other.getName() != null : !this.getName().equals(other.getName())) return false;
//    if (Double.compare(this.score, other.score) != 0) return false;
//    if (!Arrays.deepEquals(this.tags, other.tags)) return false;
//    return true;
//  }
//
//  @Override
//  public int hashCode() {
//    final int PRIME = 59;
//    int result = 1;
//    final long temp1 = Double.doubleToLongBits(this.score);
//    result = (result*PRIME) + (this.name == null ? 43 : this.name.hashCode());
//    result = (result*PRIME) + (int)(temp1 ^ (temp1 >>> 32));
//    result = (result*PRIME) + Arrays.deepHashCode(this.tags);
//    return result;
//  }
//
//  protected boolean canEqual(Object other) {
//    return other instanceof _07_EqualsAndHashCodeExample;
//  }
//
//  public static class Square extends Shape {
//    private final int width, height;
//
//    public Square(int width, int height) {
//      this.width = width;
//      this.height = height;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//      if (o == this) return true;
//      if (!(o instanceof Square)) return false;
//      Square other = (Square) o;
//      if (!other.canEqual((Object)this)) return false;
//      if (!super.equals(o)) return false;
//      if (this.width != other.width) return false;
//      if (this.height != other.height) return false;
//      return true;
//    }
//
//    @Override
//    public int hashCode() {
//      final int PRIME = 59;
//      int result = 1;
//      result = (result*PRIME) + super.hashCode();
//      result = (result*PRIME) + this.width;
//      result = (result*PRIME) + this.height;
//      return result;
//    }
//
//    protected boolean canEqual(Object other) {
//      return other instanceof Square;
//    }
//  }
//}