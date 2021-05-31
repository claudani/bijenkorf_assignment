package com.example.imageservice.service;

import com.example.imageservice.model.RequestUrlStrategy;
import com.example.imageservice.model.enums.PredefinedImageType;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@Service
@RequiredArgsConstructor
@ResponseStatus(code = HttpStatus.NOT_FOUND) //TODO - here?
public class ImageFlushServiceImpl {

  public File flush(RequestUrlStrategy requestUrl, String optimizedImage) {
    deleteReference(optimizedImage);
    if (requestUrl.isCheckOriginalEnabled()) {
      log.info("Deleting all references for dir path {}", optimizedImage);
      Arrays.stream(PredefinedImageType.values()).forEach(t -> {
        String type = "/" + t.getName() + "/";
        deleteReference(optimizedImage.replace("/original/", type));
      });
    }
    //TODO - remove all empty directories ?
    return null;
  }

  private void deleteReference(String reference) {
    try {
      Files.deleteIfExists(Paths.get(reference));
    } catch (IOException e) { // TODO
      e.printStackTrace();
    }
  }

}
