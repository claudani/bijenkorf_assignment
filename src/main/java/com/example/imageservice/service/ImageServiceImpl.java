package com.example.imageservice.service;

import com.example.imageservice.exception.CustomException;
import com.example.imageservice.model.RequestUrlStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageServiceImpl {

  @Autowired
  private BucketServiceImpl bucketService;
  @Autowired
  private ImageFlushServiceImpl flushService;
  @Autowired
  private ResizeServiceImpl resizeService;

  private String optimizedImage;
  private RequestUrlStrategy requestUrl;

  @Value("${service.awsS3Endpoint}")
  private String bucketName;
  @Value("${service.sourceRootUrl}")
  private String sourceRootUrl;

  public File handleRequest(String requestType, RequestUrlStrategy request) {
    this.requestUrl = request;
    this.optimizedImage = getExpectedFinalLocation();
    return requestType.equals("show") ? getOptimizedImage()
        : flushService.flush(requestUrl, optimizedImage);
  }

  private String getExpectedFinalLocation() {
    String result = bucketName;
    if (requestUrl.isCheckOriginalEnabled()) {
      result = result.concat("/original/");
    } else {
      result = result.concat("/" + requestUrl.getPredefinedType().getName() + "/");
    }
    return bucketService.getS3DirectoryPath(requestUrl, result);
  }

  private File getOptimizedImage() {
    Path optimizedImagePath = Paths.get(optimizedImage);
    if (Files.notExists(optimizedImagePath)) {
      requestUrl.setCheckOriginalEnabled(true);
      optimizeFromOriginalImage();
    }
    return new File(optimizedImagePath.toUri());
  }

  private void optimizeFromOriginalImage() {
    Path originalImagePath = Paths.get(
        optimizedImage.replace("/" + requestUrl.getPredefinedType().getName() + "/", "/original/"));
    if (Files.notExists(originalImagePath)) {
      bucketService.createMissingDirectories(originalImagePath.toString());
      getSourceImage(originalImagePath);
    }
    File originalImage = new File(originalImagePath.toUri());
    resizeService.resizeImage(originalImage);
    resizeService.handleResizedImage(optimizedImage);
  }

  private void getSourceImage(Path destinationPath) {
    URL url;
    try {
      url = new URL(sourceRootUrl.concat(requestUrl.getReference()));
    } catch (MalformedURLException e) {
      log.info("A URL for the path {} could not be formed", destinationPath);
      throw new CustomException();
    }
    File downloaded = downloadFromSourceRoot(url);
    bucketService.saveImage(downloaded.toPath(), destinationPath);
  }

  private File downloadFromSourceRoot(URL url) {
    String fileFormat = requestUrl.getPredefinedType().getType().getName();
    File downloaded;
    try {
      downloaded = File.createTempFile("temp_img", fileFormat);
      BufferedImage bufferedImage = ImageIO.read(Objects.requireNonNull(url));
      ImageIO.write(bufferedImage, fileFormat, downloaded);
    } catch (IOException e) {
      log.info("The requested reference ({}) does not exist on the source root url",
          FilenameUtils.getName(url.getFile()));
      try {
        Files.deleteIfExists(Paths.get(FilenameUtils.getPath(getExpectedFinalLocation())));
      } catch (IOException ioException) {
        log.info("Could not delete directories");
      }
      throw new CustomException();
    }
//    catch () { } TODO - catch source is down/server error
    return downloaded;
  }

}
