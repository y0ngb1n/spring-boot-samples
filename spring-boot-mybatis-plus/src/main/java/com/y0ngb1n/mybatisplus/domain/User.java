package com.y0ngb1n.mybatisplus.domain;

import lombok.Data;

@Data
public class User {
  private Long id;
  private String name;
  private Integer age;
  private String email;
}
