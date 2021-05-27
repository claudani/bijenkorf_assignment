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
import java.util.Objects;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
@ResponseStatus(code = HttpStatus.NOT_FOUND) //TODO - here?
public class ImageServiceImpl {

  private AmazonS3 s3client;
  private String imagePath;
  private RequestUrlStrategy requestUrl;

  @Value("${service.awsAccessKey}")
  private static String awsAccessKey;
  @Value("${service.awsSecretKey}")
  private static String awsSecretKey;
  @Value("${service.awsS3Endpoint}")
  private String bucketName;
  @Value("${service.sourceRootUrl}")
  private static String sourceRootUrl;

  public File handleRequest(RequestUrlStrategy requestUrl) {
    this.requestUrl = requestUrl;
    imagePath = requestUrl.getOptimizedImagePath();
    return getOptimizedImage();
  }

  private File getOptimizedImage() {
    Path optimizedImagePath = Paths.get("/" + bucketName + "/" + imagePath);
    if (Files.exists(optimizedImagePath)) {
      return new File(optimizedImagePath.toUri()); // TODO - uri ?
    }
    originalImageExists();
    return getOptimizedImage(); //TODO - loop?
  }

  private void originalImageExists() {
    Path originalImagePath = Paths.get(imagePath.replace("/" + requestUrl.getPredefinedType().getName() + "/", "/original/"));
    File originalImage;
//    if (s3client.doesObjectExist(bucketName, originalImagePath)) {
    if (Files.exists(originalImagePath)) {
//      S3Object object = s3client.getObject(bucketName, originalImagePath);
      originalImage = new File(originalImagePath.toUri());
    } else {
      originalImage = downloadOriginalFromSource();
    }
    // Mocked resized image - remove for AWS implementation
    Path resizedImage = Paths.get("src/main/resources/Test_image.jpg");
    Path localBucket = Paths.get(imagePath);
//    Files.copy(resizedImage.toFile(), localBucket, StandardCopyOption.REPLACE_EXISTING);
    /*TODO - resizeAndOptimize(originalImage) according to Image config document -> handle exception
       with log.warning and retry after 200ms else new log with log.error!

      Store resized image on aws
      s3client.putObject(
          bucketName,
          imagePath, //TODO - change to according to uploading structure?
          resizedImage
      );*/
  }

  private File downloadOriginalFromSource() { //TODO handle exceptions
    URL url = null;
    try {
      url = new URL(sourceRootUrl.concat(imagePath.substring(1))); // TODO - HERE
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
    File originalImage = null; //TODO get format from initial file path enum
    try {
      originalImage = File.createTempFile("temp_img", "jpg");
      BufferedImage bufferedImage = null;
      bufferedImage = ImageIO.read(Objects.requireNonNull(url));
      ImageIO.write(bufferedImage, "jpg", originalImage);
    } catch (IOException e) {
      log.error("Source {} is down or responded with an error code: {}", sourceRootUrl,
          e.getMessage());
      throw new ResponseStatusException(HttpStatus.NOT_FOUND); //TODO - double check
    }
    return originalImage;
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

  private String s3DirectoryPath() {
    String result;
    String uniqueFileName = requestUrl.getReference().replace("/", "_");

    if (imagePath.contains("/original/")) {
      result = "~/original/";
    } else {
      result = "~/" + requestUrl.getPredefinedType().name() + "/";
    }

    if (uniqueFileName.length() > 4) {
      result = result.concat(uniqueFileName.substring(0, 3) + "/");
      if (uniqueFileName.length() > 8) {
        result = result.concat(uniqueFileName.substring(4, 7) + "/");
      }
    } else {
      result = result.concat(uniqueFileName + "/");
    }
    return result.concat(uniqueFileName);
  }


  //  Code to use in actual implementation
  //  TODO - change return methods from File to S3Object
  private S3Object getOptimizedS3Image() {
    configureAwsClient();
    if (!s3client.doesObjectExist(bucketName, imagePath)) {
      originalImageExists();
    }
    return s3client.getObject(bucketName, imagePath);
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
