package com.y0ngb1n.boot.lombok.example;

import lombok.Data;

@Data
public class Mountain {

  private final String name;

  private Double latitude;

  private Double longitude;

  private String country;
}
