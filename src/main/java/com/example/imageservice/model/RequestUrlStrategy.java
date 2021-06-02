package com.example.imageservice.model;

import com.example.imageservice.exception.CustomException;
import com.example.imageservice.model.enums.PredefinedImageType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.lang.Nullable;

@Getter
@Setter
@RequiredArgsConstructor
@Slf4j
public class RequestUrlStrategy {

  private PredefinedImageType predefinedType;
  private String dummySeoName;
  private String reference;
  private boolean checkOriginalEnabled;

  public RequestUrlStrategy(String typeName, @Nullable String dummyName, String reference) {
    if (typeName.equals("original")) {
      this.predefinedType = null;
      this.checkOriginalEnabled = true;
    } else {
      typeName = typeName.replace("-", "_");
      if (!EnumUtils.isValidEnumIgnoreCase(PredefinedImageType.class, typeName)) {
        log.info("{} is not a valid predefined image type", typeName);
        throw new CustomException();
      }
      this.checkOriginalEnabled = false;
      this.predefinedType = PredefinedImageType.valueOf(typeName.toUpperCase());
    }
    this.dummySeoName = dummyName;
    this.reference = reference.replace("%2F", "/");
  }

}
