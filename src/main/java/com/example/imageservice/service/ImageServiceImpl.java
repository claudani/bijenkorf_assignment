package com.example.imageservice.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.example.imageservice.model.RequestUrlStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
@ResponseStatus(code = HttpStatus.NOT_FOUND) //TODO - here
public class ImageServiceImpl {

  private AmazonS3 s3client;
  private String optimizedImage;
  private RequestUrlStrategy requestUrl;

  @Value("${service.awsAccessKey}")
  private String awsAccessKey;
  @Value("${service.awsSecretKey}")
  private String awsSecretKey;
  @Value("${service.awsS3Endpoint}")
  private String bucketName;
  @Value("${service.sourceRootUrl}")
  private String sourceRootUrl;

  public File handleRequest(RequestUrlStrategy requestUrl) {
    this.requestUrl = requestUrl;
    optimizedImage = getExpectedFinalLocation();
    return getOptimizedImage();
  }

  private String getExpectedFinalLocation() {
    String result = bucketName;
    if (requestUrl.isCheckOriginalEnabled()) {
      result = result.concat("/original/");
    } else {
      result = result.concat("/" + requestUrl.getPredefinedType().getName() + "/");
    }
    return getS3DirectoryPath(result);
  }

  private String getS3DirectoryPath(String path) {
    String uniqueFileName = requestUrl.getReference().replace("/", "_");
    if (FilenameUtils.getBaseName(uniqueFileName).length() > 3) {
      path = path.concat(uniqueFileName.substring(0, 4) + "/");
      if (FilenameUtils.getBaseName(uniqueFileName).length() > 7) {
        path = path.concat(uniqueFileName.substring(4, 8) + "/");
      }
    } else {
      path = path.concat(FilenameUtils.getBaseName(uniqueFileName) + "/");
    }
    return path.concat(uniqueFileName);
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
    File originalImage;
    if (Files.notExists(originalImagePath)) {
      createMissingDirectories(originalImagePath.toString());
      getSourceImage(originalImagePath);
    }
    originalImage = new File(originalImagePath.toUri());
    resizeImage(originalImage);
    handleResizedImage();
  }

  private void createMissingDirectories(String imagePath) {
    File f = new File(imagePath);
    if (Objects.nonNull(f.getParentFile())) {
      f.getParentFile().mkdirs();
    }
  }

  private void handleResizedImage() {
    // Mocked resized image - remove for AWS implementation
    Path resizedImage = Paths.get("src/main/resources/resized_image.jpg");
    Path newlyCreatedDir = Paths.get(optimizedImage);
    createMissingDirectories(optimizedImage);
    saveImageToBucket(resizedImage, newlyCreatedDir);
  }

  private void saveImageToBucket(Path source, Path destination) {
    try {
      Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) { //TODO - handle exception better
      e.printStackTrace();
    }
  }

  private void resizeImage(File originalImage) {
    log.info(
        "TODO - This method would resize and optimize the originalImage according to the Image "
            + "config document and would handle exceptions by logging at .warning, the would retry after"
            + "200ms, otherwise log an error: {}", originalImage.exists());
  }

  private void getSourceImage(Path destinationPath) {
    URL url = null;
    try {
      url = new URL(sourceRootUrl.concat(requestUrl.getReference()));
    } catch (MalformedURLException e) { //TODO handle exceptions
      e.printStackTrace();
    }
    File downloaded = downloadFromSourceRoot(url);
    saveImageToBucket(downloaded.toPath(), destinationPath);
  }

  private File downloadFromSourceRoot(URL url) {
    String fileFormat = requestUrl.getPredefinedType().getType().getName();
    File downloaded;
    try {
      downloaded = File.createTempFile("temp_img", fileFormat);
      BufferedImage bufferedImage = ImageIO.read(Objects.requireNonNull(url));
      ImageIO.write(bufferedImage, fileFormat, downloaded);
    } catch (IOException e) {
      log.error("Source {} is down or responded with an error code: {}", sourceRootUrl,
          e.getMessage());
      throw new ResponseStatusException(HttpStatus.NOT_FOUND); //TODO - double check
    }
    return downloaded;
  }

  public void flush(String typeName, String reference) {
    if (typeName.contains("/original/")) {
      // TODO - all optimized images will be removed for this reference
      //  https://www.baeldung.com/aws-s3-java#6-deleting-multiple-objects
    } else {
      // TODO - remove typeName files
      s3client.deleteObject(bucketName, reference);
    }
  }

  /* ---------------------------------------------------------------------------------------------- */

  //  Code to use in implementation using a AWS S3 Bucket
  //  => change return methods from 'File' to 'S3Object'
  private S3Object getOptimizedS3Image() {
    configureAwsClient();
    if (!s3client.doesObjectExist(bucketName, optimizedImage)) {
      optimizeFromOriginalImage();
    }
    return s3client.getObject(bucketName, optimizedImage);
  }

  private void configureAwsClient() {
    AWSCredentials awsCredentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey);
    s3client = AmazonS3ClientBuilder
        .standard()
        .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
        .withRegion(Regions.EU_CENTRAL_1)
        .build();
  }

}
