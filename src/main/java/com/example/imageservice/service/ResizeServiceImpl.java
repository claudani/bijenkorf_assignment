package com.example.imageservice.service;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@Service
@RequiredArgsConstructor
@ResponseStatus(code = HttpStatus.NOT_FOUND) //TODO - here?
public class ResizeServiceImpl {

  @Autowired
  private BucketServiceImpl bucketService;

  void resizeImage(File originalImage) {
    log.info(
        "TODO - This method would resize and optimize the originalImage according to the Image "
            + "config document and would handle exceptions by logging at .warning, the would retry after"
            + "200ms, otherwise log an error: {}", originalImage.exists());
  }

  void handleResizedImage(String optimizedImage) {
    // Mocked resized image - remove for AWS implementation
    Path resizedImage = Paths.get("src/main/resources/resized_image.jpg");
    Path newlyCreatedDir = Paths.get(optimizedImage);
    bucketService.createMissingDirectories(optimizedImage);
    bucketService.saveImage(resizedImage, newlyCreatedDir);
  }

}
