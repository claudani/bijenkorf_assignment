package com.example.imageservice.model;

import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class S3DirectoryStrategy {

  private String predefinedTypeName;
  private String firstFourCharacters;
  private String secondFourCharacters;
  private String uniqueOriginalImageFilename;


  private String s3Directory() {
    if (Objects.isNull(secondFourCharacters)) {
      return predefinedTypeName + "/" + firstFourCharacters + "/" + uniqueOriginalImageFilename;
    }
    return predefinedTypeName + "/" + firstFourCharacters + "/" + secondFourCharacters + "/"
        + uniqueOriginalImageFilename;
  }

}
