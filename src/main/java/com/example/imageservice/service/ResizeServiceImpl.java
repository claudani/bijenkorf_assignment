package com.example.imageservice.service;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResizeServiceImpl {

  @Autowired
  private BucketServiceImpl bucketService;

  void resizeImage(File originalImage) {
    log.info(
        "TODO - This method would resize and optimize the originalImage {} according to requirements",
        originalImage.getName());
  }

  void handleResizedImage(String optimizedImage) {
    // Mocked resized image, remove for AWS implementation
    Path resizedImage = Paths.get("src/main/resources/resized_image.jpg");
    Path newlyCreatedDir = Paths.get(optimizedImage);
    bucketService.createMissingDirectories(optimizedImage);
    bucketService.saveImage(resizedImage, newlyCreatedDir);
  }

}
