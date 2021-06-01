package com.example.imageservice.model.enums;

public enum Type {
  JPG("jpg"),
  PNG("png");

  private final String name;

  Type(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

}
