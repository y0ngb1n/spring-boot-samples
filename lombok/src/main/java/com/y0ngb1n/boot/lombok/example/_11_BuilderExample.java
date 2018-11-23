package com.y0ngb1n.boot.lombok.example;

import java.util.Set;
import lombok.Builder;
import lombok.Singular;

/**
 * builder 是现在比较推崇的一种构建值对象的方式.
 *
 * 注：生成器模式
 *
 * @see lombok.Builder
 */
@Builder
public class _11_BuilderExample {

  @Builder.Default
  private long created = System.currentTimeMillis();

  private String name;

  private int age;

  @Singular
  private Set<String> occupations;
}

//  翻译成 Java 程序是：
//public class _11_BuilderExample {
//  private long created;
//  private String name;
//  private int age;
//  private Set<String> occupations;
//
//  _11_BuilderExample(String name, int age, Set<String> occupations) {
//    this.name = name;
//    this.age = age;
//    this.occupations = occupations;
//  }
//
//  private static long $default$created() {
//    return System.currentTimeMillis();
//  }
//
//  public static _11_BuilderExampleBuilder builder() {
//    return new _11_BuilderExampleBuilder();
//  }
//
//  public static class _11_BuilderExampleBuilder {
//    private long created;
//    private boolean created$set;
//    private String name;
//    private int age;
//    private ArrayList<String> occupations;
//
//    _11_BuilderExampleBuilder() {
//    }
//
//    public _11_BuilderExampleBuilder created(long created) {
//      this.created = created;
//      this.created$set = true;
//      return this;
//    }
//
//    public _11_BuilderExampleBuilder name(String name) {
//      this.name = name;
//      return this;
//    }
//
//    public _11_BuilderExampleBuilder age(int age) {
//      this.age = age;
//      return this;
//    }
//
//    public _11_BuilderExampleBuilder occupation(String occupation) {
//      if (this.occupations == null) {
//        this.occupations = new ArrayList<String>();
//      }
//
//      this.occupations.add(occupation);
//      return this;
//    }
//
//    public _11_BuilderExampleBuilder occupations(Collection<? extends String> occupations) {
//      if (this.occupations == null) {
//        this.occupations = new ArrayList<String>();
//      }
//
//      this.occupations.addAll(occupations);
//      return this;
//    }
//
//    public _11_BuilderExampleBuilder clearOccupations() {
//      if (this.occupations != null) {
//        this.occupations.clear();
//      }
//
//      return this;
//    }
//
//    public _11_BuilderExample build() {
//      // complicated switch statement to produce a compact properly sized immutable set omitted.
//      // go to https://projectlombok.org/features/Singular-snippet.html to see it.
//      Set<String> occupations = ...;
//      return new _11_BuilderExample(created$set ? created : _11_BuilderExample.$default$created(), name, age, occupations);
//    }
//
//    @Override
//    public String toString() {
//      return "_11_BuilderExample._11_BuilderExampleBuilder(created = " + this.created + ", name = " + this.name + ", age = " + this.age + ", occupations = " + this.occupations + ")";
//    }
//  }
//}