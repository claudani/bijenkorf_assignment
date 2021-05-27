package com.example.imageservice.model;

import com.example.imageservice.model.enums.PredefinedImageType;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.server.ResponseStatusException;

@Getter
@Setter
@RequiredArgsConstructor
@Slf4j
public class RequestUrlStrategy {

  private PredefinedImageType predefinedType;
  private String dummySeoName;
  private String reference;

  public RequestUrlStrategy(String typeName, @Nullable String dummyName, String reference) {
    if (!EnumUtils.isValidEnum(PredefinedImageType.class, typeName.toUpperCase())) {
      log.info("{} is not a valid predefined image type", typeName);
      throw new ResponseStatusException(HttpStatus.NOT_FOUND); //TODO - double check
    }
    this.predefinedType = PredefinedImageType.valueOf(typeName.toUpperCase());
    this.dummySeoName = dummyName;
    this.reference = reference.replace("%2F", "/");
  }

  public String getOptimizedImagePath() {
    if (Objects.nonNull(dummySeoName)) {
      return "/" + predefinedType.getName() + "/" + dummySeoName + "/" + reference;
    }
    return "/" + predefinedType.getName() + "/" + reference;
  }

}
