package com.y0ngb1n.boot.lombok.example;

import lombok.Getter;
import lombok.Setter;

public class User {

  @Getter
  private String name;

  @Getter
  private String nick;

  @Getter
  @Setter
  private Boolean loggedIn;
}
