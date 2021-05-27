package com.example.imageservice.model.enums;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;

@Getter
public enum PredefinedImageType {
  // Enum fields
  DETAIL_LARGE(100, 100, 1000, ScaleType.FILL, "0000FF", Type.PNG),
  THUMBNAIL(10, 10, 100, ScaleType.CROP, "FF0000", Type.JPG);


  // Internal state
  public static final PredefinedImageType DEFAULT_TYPE = THUMBNAIL;
  private final int height;
  private final int width;
  private final int quality; // TODO - 0-100
  private final ScaleType scaleType;
  private final String fillColor; // todo - depends on ScaleType
  private final Type type;

  @Value("${service.sourceRootUrl}")
  private static String sourceName;

  // Constructor
  PredefinedImageType(int height, int width, int quality, ScaleType scaleType, String fillColor,
      Type type) {
    this.height = height;
    this.width = width;
    this.quality = quality;
    this.scaleType = scaleType;
    this.fillColor = fillColor;
    this.type = type;
  }

  public String getName() {
    return this.name().toLowerCase();
  }

}
